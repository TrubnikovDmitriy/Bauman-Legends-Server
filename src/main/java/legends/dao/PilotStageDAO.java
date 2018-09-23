package legends.dao;

import legends.responseviews.PilotTask;
import org.springframework.dao.DataAccessException;
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
		} catch (DataAccessException e) {
			// TODO: Обработать ситуацию, когда разогрев начался,
			// TODO: а задания у команды до сих пор нет.
			throw e;
		}
	}

//	public boolean checkAnswer(Answer answer) {
//		jdbcTemplate.queryForObject(
//				"SELECT ",
//				new Object[] {},
//				Integer.class
//		);
//	}

//	private synchronized Integer startNextTask(Integer teamID, boolean fail) {
//		jdbcTemplate.query(
//				"SELECT FROM"
//		)
//	}
}
