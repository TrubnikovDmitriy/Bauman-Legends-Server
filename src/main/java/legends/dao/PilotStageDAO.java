package legends.dao;

import legends.requestviews.Answer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PilotStageDAO {

	private final JdbcTemplate jdbcTemplate;

	public PilotStageDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
