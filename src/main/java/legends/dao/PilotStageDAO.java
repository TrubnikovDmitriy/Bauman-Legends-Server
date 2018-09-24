package legends.dao;

import legends.responseviews.PilotTask;
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
					"SELECT task_id, start_time, points, type " +
							"FROM current_tasks WHERE team_id=? AND success IS NULL",
					new Object[] { teamID },
					new PilotTask.Mapper()
			);
		} catch (EmptyResultDataAccessException e) {
			// TODO: Обработать ситуацию, когда разогрев начался,
			// TODO: а задания у команды до сих пор нет.
			throw e;
		}
	}
}
