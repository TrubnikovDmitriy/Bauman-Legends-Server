package legends.dao;

import legends.Configuration;
import legends.exceptions.PhotoKeyDoesNotExist;
import legends.exceptions.TeamAlreadyStarted;
import legends.models.TaskType;
import legends.responseviews.PilotTask;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class PilotStageDAO {

	private final JdbcTemplate jdbcTemplate;

	public PilotStageDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public PilotTask getCurrentTask(final Integer teamID) {
		try {
			return jdbcTemplate.queryForObject(
					"SELECT task_id, points, ts.type " +
							"FROM current_tasks JOIN tasks ts ON task_id=ts.id " +
							"WHERE team_id=? AND success IS NULL AND ts.type<>?",
					new Object[] { teamID, TaskType.FINAL.name() },
					new PilotTask.Mapper()
			);
		} catch (EmptyResultDataAccessException e) {
			// TODO: Обработать ситуацию, когда разогрев начался,
			// TODO: а задания у команды до сих пор нет.
			// TODO: Или когда она прошла уже все этапы.
			throw e;
		}
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

		} catch (DataAccessException e) {
			// TODO: Обработать ситуацию, когда current task'a нет или их несколько
			throw e;
		}
	}

	public synchronized void startTeam(final Integer teamID) {
		try {
			jdbcTemplate.update(
					"INSERT INTO current_tasks(task_id, team_id, start_time, success, type, finish_time) " +
						"VALUES ((SELECT t.final_tasks_arr[1] FROM teams t WHERE t.id=?), ?, ?, NULL, 'FINAL', NULL)",
					teamID, teamID, Configuration.currentTimestamp()
			);
			jdbcTemplate.update(
					"UPDATE teams SET start_time=?, started=TRUE WHERE id=?",
					Configuration.currentTimestamp(), teamID
			);
		} catch (DuplicateKeyException ignore) {
			throw new TeamAlreadyStarted(teamID);
		}
	}
}
