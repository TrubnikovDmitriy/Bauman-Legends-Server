package legends.dao;

import legends.models.TaskType;
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
					"SELECT task_id, ct.type, start_time, points, duration " +
							"FROM current_tasks ct JOIN tasks ts ON task_id=ts.id " +
							"WHERE team_id=? AND success IS NULL AND ts.type=?",
					new Object[] { teamID, TaskType.FINAL.name() },
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
