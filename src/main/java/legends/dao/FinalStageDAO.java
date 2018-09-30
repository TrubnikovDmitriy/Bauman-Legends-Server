package legends.dao;

import legends.Configuration;
import legends.exceptions.CriticalInternalError;
import legends.exceptions.TaskIsAlreadyAnswered;
import legends.exceptions.TeamAlreadyStarted;
import legends.models.NewTask;
import legends.models.TaskType;
import legends.responseviews.FinalTask;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
public class FinalStageDAO {

	private final JdbcTemplate jdbcTemplate;
	private final ScheduledExecutorService scheduler;

	public FinalStageDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	@Nullable
	public FinalTask getCurrentTask(final Integer teamID) {
		try {
			// Take the last task for team
			final FinalTask task = jdbcTemplate.queryForObject(
					"SELECT task_id, ct.type, ct.start_time, points, duration, success, " +
							"  final_tasks_arr[array_length(final_tasks_arr, 1)] AS last_task_id " +
							"FROM current_tasks ct " +
							"  JOIN tasks ts ON ts.id=ct.task_id " +
							"  JOIN teams tm ON tm.id=ct.team_id " +
							"WHERE team_id=? AND ct.type=? " +
							"ORDER BY ct.id DESC LIMIT 1",
					new Object[] { teamID, TaskType.FINAL.name() },
					new FinalTask.Mapper()
			);

			if (task == null) throw new CriticalInternalError(this.getClass().getSimpleName());

			return task;

		} catch (EmptyResultDataAccessException ignore) {
			// It is mean that team is not yet started
			return null;
		}
	}

	public boolean checkAnswer(final Integer taskID,
	                           final String playerAnswer) {

		final String correctAnswers = jdbcTemplate.queryForObject(
				"SELECT answers FROM tasks WHERE id=?",
				new Object[] { taskID },
				String.class
		);
		if (correctAnswers == null) {
			throw new CriticalInternalError(
					"Если Вы это видите, значит Вы - счастливчики :)." +
							"Напишите Трубникову Диме 'vk.com/trubnikovdv' со словами: " +
							"\"Check answer final, taskID=" + taskID + "\"."
			);
		}
		final List<String> answersList = Arrays.asList(
				correctAnswers.split(Configuration.SEPARATOR));
		return answersList.contains(playerAnswer.toLowerCase());
	}

	@Transactional
	public synchronized void startTeam(final Integer teamID) {
		try {
			insertNewCurrentTask(teamID, 1);
			jdbcTemplate.update(
					"UPDATE teams SET start_time=?, started=TRUE WHERE id=?",
					Configuration.currentTimestamp(), teamID
			);
		} catch (DuplicateKeyException ignore) {
			throw new TeamAlreadyStarted(teamID);
		}
	}


	@Transactional
	public synchronized void acceptCurrentTask(final Integer teamID,
	                                           final Integer currentTaskID) {
		try {
			// It is necessary to make sure that the question is not yet answered
			// So this `queryForObject` prevents the race condition
			jdbcTemplate.queryForObject(
					"SELECT id FROM current_tasks " +
							"WHERE team_id=? AND task_id=? AND success IS NULL",
					new Object[]{teamID, currentTaskID},
					Integer.class
			);

			// Close the current task with boolean value 'true'
			jdbcTemplate.update(
					"UPDATE current_tasks SET (success, finish_time) = (TRUE, ?) " +
							"WHERE team_id=? AND task_id=? AND success IS NULL",
					Configuration.currentTimestamp(), teamID, currentTaskID
			);

			// Increase score of team
			jdbcTemplate.update(
					"UPDATE teams tms SET " +
							"score=score+(SELECT points FROM tasks tsk WHERE tsk.id=?) " +
							"WHERE tms.id=?",
					currentTaskID, teamID
			);

		} catch (EmptyResultDataAccessException ignore) {
			throw new TaskIsAlreadyAnswered();
		}
	}

	@Transactional
	private synchronized void rejectCurrentTask(final Integer teamID,
	                                            final Integer currentTaskID) {
		try {
			// It is necessary to make sure that the question is not yet answered
			// So this `queryForObject` prevents the race condition
			jdbcTemplate.queryForObject(
					"SELECT id FROM current_tasks " +
							"WHERE team_id=? AND task_id=? AND success IS NULL",
					new Object[]{teamID, currentTaskID},
					Integer.class
			);
		} catch (EmptyResultDataAccessException ignore) {
			// This means that the team managed to answer the question in time
			// But we need to make sure they take the next task.
			final FinalTask lastTask = this.getCurrentTask(teamID);
			if (lastTask == null) {
				// TODO logger
				throw new CriticalInternalError("Кто-то шестерит руками в БД");
			}
			if (lastTask.getID() == currentTaskID && !lastTask.isFinished()) {
				this.takeNextTask(teamID, currentTaskID);
			}
			return;
		}

		// Close the current task with boolean value 'false'
		jdbcTemplate.update(
				"UPDATE current_tasks SET (success, finish_time) = (FALSE, ?) " +
						"WHERE team_id=? AND task_id=? AND success IS NULL",
				Configuration.currentTimestamp(), teamID, currentTaskID
		);

		// Increment the fails counter for team
		jdbcTemplate.update(
				"UPDATE teams SET fails_count=fails_count+1 WHERE id=?",
				teamID
		);

		// Immediately take the next task
		this.takeNextTask(teamID, currentTaskID);
	}

	public synchronized boolean isAnswered(final Integer teamID,
	                                       final Integer taskID) {
		final List<Boolean> success = jdbcTemplate.query(
				"SELECT success FROM current_tasks " +
						"WHERE team_id=? AND task_id=? AND success IS NOT NULL",
				new Object[] { teamID, taskID },
				(rs, i) -> rs.getBoolean("success")
		);
		return !success.isEmpty();
	}


	@Transactional
	public synchronized void takeNextTask(final Integer teamID,
	                                      final Integer currentTaskID) {
		// Take array of team's tasks
		Integer[] tasksArrs = jdbcTemplate.queryForObject(
				"SELECT final_tasks_arr FROM teams WHERE id=?",
				new Object[] { teamID },
				(rs, i) -> (Integer[]) rs.getArray("final_tasks_arr").getArray()
		);

		// Try to take the index of current task
		if (tasksArrs == null) tasksArrs = new Integer[] { };
		final List<Integer> taskList = Arrays.asList(tasksArrs);
		final int currentTaskIndex = taskList.indexOf(currentTaskID) + 1; // [0; n) -> [1; n+1)
		if (currentTaskIndex == 0) throw new CriticalInternalError(
				"Если Вы получили это сообщение, значит Вы - счастливчики :)" +
						"Напишите Диме Трубникову 'vk.com/trubnikovdv' со словами: " +
						"teamID=" + teamID + ", " + "currentTaskID=" + currentTaskID
		);

		// If it wasn't the last task
		if (currentTaskIndex < taskList.size()) {
			insertNewCurrentTask(teamID, currentTaskIndex + 1);
		}
	}

	private synchronized void insertNewCurrentTask(final Integer teamID,
	                                               final Integer taskIndex) {
		// Indexing in the database begins with ONE!

		final NewTask newTask;
		try {
			// Take info about new task (ID and duration)
			newTask = jdbcTemplate.queryForObject(
					"SELECT id, duration FROM tasks " +
							"WHERE id=(SELECT t.final_tasks_arr[?] FROM teams t WHERE t.id=?)",
					new Object[] { taskIndex, teamID },
					(rs, i) -> new NewTask(
							teamID,
							rs.getInt("id"),
							rs.getInt("duration")
					)
			);
			if (newTask == null) throw new CriticalInternalError(
					"Если Вы это видите - значит Вы счастливчики :)." +
							"Напишите Трубникову 'vk.com/trubnikovdv' и скажите, " +
							"что не удалось перейти к следующему заданию."
			);

			// Insering new task
			jdbcTemplate.update(
					"INSERT INTO current_tasks(task_id, team_id, start_time, success, type, finish_time) " +
							"VALUES (?, ?, ?, NULL, ?, NULL)",
					newTask.taskID, teamID, Configuration.currentTimestamp(), TaskType.FINAL.name()
			);

		} catch (DuplicateKeyException ignore) {
			// Catch exception after inserting
			// (pair team_id and task_id is unique)
			throw new TaskIsAlreadyAnswered();
		}

		// Run the timer for this task
		scheduler.schedule(
				() -> this.rejectCurrentTask(newTask.teamID, newTask.taskID),
				newTask.duration,
				TimeUnit.SECONDS
		);
	}
}
