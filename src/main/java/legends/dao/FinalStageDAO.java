package legends.dao;

import legends.responseviews.FinalTask;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class FinalStageDAO {

	private final JdbcTemplate jdbcTemplate;

	public FinalStageDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public FinalTask getCurrentTask(final Integer teamID) {
		try {
			return jdbcTemplate.queryForObject(
					"SELECT task_id, type, start_time, points, duration " +
							"FROM current_tasks WHERE team_id=? AND success IS NULL",
					new Object[] { teamID },
					new FinalTask.Mapper()
			);
		} catch (EmptyResultDataAccessException e) {
			// TODO: Обработать ситуацию, когда финал начался,
			// TODO: а задания у команды до сих пор нет (еще один SQL запрос,
			// TODO: чтобы понять либо они уже завершили, либо еще наступило их время)
			throw e;
		}
	}
}
