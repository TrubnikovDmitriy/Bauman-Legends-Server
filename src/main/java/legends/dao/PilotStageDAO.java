package legends.dao;

import legends.exceptions.PhotoKeyDoesNotExist;
import legends.models.TaskType;
import legends.responseviews.PilotTask;
import legends.responseviews.StartingTeam;
import org.springframework.dao.DataAccessException;
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

	public StartingTeam prepareTeam(final Integer teamID) {
		return jdbcTemplate.queryForObject(
				"SELECT id, name, start_time, started, finished, " +
						"(SELECT COUNT(id) FROM players WHERE team_id=? GROUP BY team_id) AS players_count, " +
						"(SELECT COUNT(id) FROM current_tasks WHERE type='PHOTO' AND team_id=? GROUP BY team_id) AS photo_count, " +
						"(SELECT COUNT(id) FROM current_tasks WHERE type='EXTRA' AND team_id=? GROUP BY team_id) AS extra_count " +
						"FROM teams WHERE id=?;",
				new Object[] { teamID, teamID, teamID, teamID },
				new StartingTeam.Mapper()
		);
	}

	public synchronized void stopPilotStage() {
		jdbcTemplate.update(
				"UPDATE current_tasks SET success=FALSE WHERE success IS NULL AND (type=? OR type=?)",
				TaskType.PHOTO.name(), TaskType.EXTRA.name()
		);
	}
}
