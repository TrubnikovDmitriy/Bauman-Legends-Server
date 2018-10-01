package legends.dao;

import legends.Configuration;
import legends.exceptions.CriticalInternalError;
import legends.exceptions.PhotoKeyDoesNotExist;
import legends.exceptions.TaskIsAlreadyAnswered;
import legends.exceptions.TeamDoesNotExist;
import legends.models.TaskType;
import legends.responseviews.PilotTask;
import legends.responseviews.Result;
import legends.responseviews.StartingTeam;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;


@Repository
public class PilotStageDAO {

	private final JdbcTemplate jdbcTemplate;

	public PilotStageDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getPhotoKey(final Integer teamID) {
		try {
			return jdbcTemplate.queryForObject(
					"SELECT ts.answers FROM current_tasks ct " +
							"JOIN tasks ts ON task_id=ts.id " +
							"WHERE team_id=? AND success IS NULL AND ct.type=?",
					new Object[] { teamID, TaskType.PHOTO.name() },
					String.class
			);

		} catch (EmptyResultDataAccessException e) {
			throw new PhotoKeyDoesNotExist(e, teamID);

		} catch (IncorrectResultSizeDataAccessException ignore) {
			throw new CriticalInternalError(
					"Произошла ошибка. Команда №" + teamID + ' ' +
					"выполняет сразу несколько фотоквестов. " +
					"Срочно напишите Трубникову Диме"
			);
		}
	}

	public StartingTeam prepareTeam(final Integer teamID) {
		try {
			return jdbcTemplate.queryForObject(
					"SELECT id, name, start_time, started, finished, " +
							"(SELECT COUNT(id) FROM players WHERE team_id=? GROUP BY team_id) AS players_count, " +
							"(SELECT COUNT(id) FROM current_tasks WHERE type='PHOTO' AND success=TRUE AND team_id=? GROUP BY team_id) AS photo_count, " +
							"(SELECT COUNT(id) FROM current_tasks WHERE type='EXTRA' AND success=TRUE AND team_id=? GROUP BY team_id) AS extra_count " +
							"FROM teams WHERE id=?;",
					new Object[]{teamID, teamID, teamID, teamID},
					new StartingTeam.Mapper()
			);
		} catch (EmptyResultDataAccessException e) {
			throw new TeamDoesNotExist(e, teamID);
		}
	}

	public void startPilotStage() {
		// TODO DELETE it is in prodcuction!!!
		jdbcTemplate.update("DELETE FROM current_tasks");
		jdbcTemplate.update(
				"INSERT INTO current_tasks (team_id, task_id, start_time, type) " +
				"SELECT id, pilot_tasks_arr[1], ?, 'PHOTO' FROM teams",
				Configuration.currentTimestamp()
		);
		jdbcTemplate.update("UPDATE teams SET (started, finished)=(TRUE, FALSE)");
	}

	public synchronized void stopPilotStage() {
		jdbcTemplate.update(
				"UPDATE current_tasks SET success=FALSE WHERE success IS NULL AND (type=? OR type=?)",
				TaskType.PHOTO.name(), TaskType.EXTRA.name()
		);
	}

	public @NotNull PilotTask getCurrentTask(final Integer teamID) {
		try {
			// Take the last pilot task for team
			final PilotTask task = jdbcTemplate.queryForObject(
					"SELECT task_id, ct.type, points, success, " +
							"pilot_tasks_arr[array_length(pilot_tasks_arr, 1)] AS last_task_id " +
							"FROM current_tasks ct " +
							"  JOIN tasks ts ON ts.id=ct.task_id " +
							"  JOIN teams tm ON tm.id=ct.team_id " +
							"WHERE team_id=? AND (ct.type=? OR ct.type=?) " +
							"ORDER BY ct.id DESC LIMIT 1",
					new Object[] { teamID, TaskType.PHOTO.name(), TaskType.EXTRA.name() },
					new PilotTask.Mapper()
			);

			if (task == null) throw new CriticalInternalError(this.getClass().getSimpleName());
			return task;

		} catch (EmptyResultDataAccessException ignore) {
			throw new CriticalInternalError(
					"Если Вы это видите, значит Вы - счастливчики :)." +
					"Напишите Трубникову Диме 'vk.com/trubnikovdv', " +
					"что у Вас еще нет заданий разогревочного этапа."
			);
		}
	}

	public Result checkAnswer(final Integer taskID,
	                          final String playerAnswer,
	                          final TaskType taskType) {

		final String correctAnswers = jdbcTemplate.queryForObject(
				"SELECT answers FROM tasks WHERE id=?",
				new Object[] { taskID },
				String.class
		);
		if (correctAnswers == null) {
			throw new CriticalInternalError(
					"Если Вы это видите, значит Вы - счастливчики :)." +
							"Напишите Трубникову Диме 'vk.com/trubnikovdv' со словами: " +
							"\"Check answer pilot, taskID=" + taskID + "\"."
			);
		}
		final List<String> answersList = Arrays.asList(
				correctAnswers.split(Configuration.SEPARATOR));

		final boolean isCorrect = (playerAnswer != null) &&
				answersList.contains(playerAnswer.toLowerCase());

		String tooltip = null;
		// Extra tasks should return tooltips
		if (taskType == TaskType.EXTRA) {
			tooltip = jdbcTemplate.queryForObject(
					"SELECT tooltip FROM tooltips WHERE extra_id=?",
					new Object[] { taskID },
					String.class
			);
		}
		return new Result(isCorrect, taskType, tooltip);
	}

	@Transactional
	public synchronized void acceptCurrentAndTakeNextTasks(final Integer teamID,
	                                                       final Integer currentTaskID,
	                                                       final Boolean success) {
		acceptCurrentTask(teamID, currentTaskID, success);
		takeNextTask(teamID, currentTaskID);
	}

	private void takeNextTask(final Integer teamID,
	                          final Integer currentTaskID) {
		// Take array of team's tasks
		Integer[] tasksArrs = jdbcTemplate.queryForObject(
				"SELECT pilot_tasks_arr FROM teams WHERE id=?",
				new Object[] { teamID },
				(rs, i) -> (Integer[]) rs.getArray("pilot_tasks_arr").getArray()
		);

		// Try to take the index of current task
		if (tasksArrs == null) tasksArrs = new Integer[] { };
		final List<Integer> pilotTasks = Arrays.asList(tasksArrs);
		final int currentTaskIndex = pilotTasks.indexOf(currentTaskID) + 1; // [0; n) -> [1; n+1)
		if (currentTaskIndex == 0) throw new CriticalInternalError(
				"Если Вы получили это сообщение, значит Вы - счастливчики :)" +
						"Напишите Диме Трубникову 'vk.com/trubnikovdv' со словами: " +
						"teamID=" + teamID + ", " + "currentTaskID=" + currentTaskID
		);

		// Even number - PHOTO, odd number - EXTRA
		final TaskType taskType = (currentTaskIndex + 1) % 2 == 0 ?
				TaskType.EXTRA : TaskType.PHOTO;

		// If not all tasks are completed
		if (currentTaskIndex < pilotTasks.size()) {
			insertNewCurrentTask(teamID, currentTaskIndex + 1, taskType);
		}
	}

	private void acceptCurrentTask(final Integer teamID,
	                               final Integer currentTaskID,
	                               final Boolean success) {
		// Close the current task with boolean value 'success'
		jdbcTemplate.update(
				"UPDATE current_tasks SET (success, finish_time) = (?, ?) " +
						"WHERE team_id=? AND task_id=?",
				success, Configuration.currentTimestamp(), teamID, currentTaskID
		);
		jdbcTemplate.update(
				"UPDATE teams tms SET score=score+(SELECT points FROM tasks tsk WHERE tsk.id=?) WHERE tms.id=?",
				currentTaskID, teamID
		);
	}

	private synchronized void insertNewCurrentTask(final Integer teamID,
	                                               final Integer taskIndex,
	                                               final TaskType type) {
		try {
			// Indexing in the database begins with ONE!
			jdbcTemplate.update(
					"INSERT INTO current_tasks(task_id, team_id, start_time, success, type, finish_time) " +
							"VALUES ((SELECT t.pilot_tasks_arr[?] FROM teams t WHERE t.id=?), ?, ?, NULL, ?, NULL)",
					taskIndex, teamID, teamID, Configuration.currentTimestamp(), type.name()
			);

		} catch (DuplicateKeyException ignore) {
			throw new TaskIsAlreadyAnswered();
		}
	}
}
