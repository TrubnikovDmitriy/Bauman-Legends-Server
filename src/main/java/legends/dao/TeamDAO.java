package legends.dao;

import legends.responseviews.TeamTable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamDAO {

	private final JdbcTemplate jdbcTemplate;

	public TeamDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<TeamTable> getTeams(final boolean fullList) {
		if (fullList) {
			return jdbcTemplate.query(
					"SELECT id, name, score, leader_name, " +
							"current_final_task_id, current_begin_task_id, final_tasks_arr, " +
							"start_time, finish_time, fails_count FROM teams",
					new TeamTable.Mapper()
			);
		} else {
			return jdbcTemplate.query(
					"SELECT id, name, score, leader_name, current_final_task_id, " +
							"current_begin_task_id, final_tasks_arr, " +
							"start_time, finish_time, fails_count " +
							"FROM teams WHERE current_begin_task_id IS NOT NULL",
					new TeamTable.Mapper()
			);
		}
	}
}
