package legends.dao;

import legends.models.TeamForTable;
import legends.responseviews.TeamInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamDAO {

	private final JdbcTemplate jdbcTemplate;

	public TeamDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<TeamForTable> getTeams(final boolean fullList) {
		if (fullList) {
			return jdbcTemplate.query(
					"SELECT id, name, score, leader_name, " +
							"current_final_task_id, current_begin_task_id, final_tasks_arr, " +
							"start_time, finish_time, fails_count FROM teams",
					new TeamForTable.Mapper()
			);
		} else {
			return jdbcTemplate.query(
					"SELECT id, name, score, leader_name, current_final_task_id, " +
							"current_begin_task_id, final_tasks_arr, " +
							"start_time, finish_time, fails_count " +
							"FROM teams WHERE current_begin_task_id IS NOT NULL",
					new TeamForTable.Mapper()
			);
		}
	}

	public TeamInfo getTeamForModerator(final Integer teamID) {
		final List<String> members = jdbcTemplate.query(
				"SELECT first_name, second_name FROM players WHERE team_id=?",
				new Object[] { teamID },
				(rs, n) -> rs.getString(1) + ' ' + rs.getString(2)
		);
		return jdbcTemplate.queryForObject(
				"SELECT t.id id, name, leader_name, score, start_time, " +
						"a.login login, a.pass pass FROM teams AS t " +
						"JOIN auth AS a ON t.id = a.team_id WHERE t.id=?",
				new Object[]{teamID},
				new TeamInfo.Mapper(members)
		);
	}

	public TeamInfo getTeamForPlayer(final Integer teamID) {
		final TeamInfo team = getTeamForModerator(teamID);
		team.eraseLoginPass();
		return team;
	}

}
