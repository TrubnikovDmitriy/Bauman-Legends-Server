package legends.dao;

import legends.exceptions.TeamDoesNotExist;
import legends.models.TeamForTable;
import legends.responseviews.TeamInfo;
import org.springframework.dao.EmptyResultDataAccessException;
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
					"SELECT id, name, score, leader_name, final_tasks_arr, start_time, finish_time, fails_count, finished, started, " +
							"(SELECT COUNT(ct.id) FROM current_tasks AS ct WHERE ct.type='FINAL') AS task_count " +
							"FROM teams",
					new TeamForTable.Mapper()
			);
		} else {
			return jdbcTemplate.query(
					"SELECT id, name, score, leader_name, final_tasks_arr, start_time, finish_time, fails_count, finished, started, " +
							"(SELECT COUNT(ct.id) FROM current_tasks AS ct WHERE ct.type='FINAL') AS task_count " +
							"FROM teams WHERE started=TRUE AND finished=FALSE",
					new TeamForTable.Mapper()
			);
		}
	}

	public TeamInfo getTeamForModerator(final Integer teamID) {
		try {
			final List<String> members = jdbcTemplate.query(
					"SELECT first_name, second_name FROM players WHERE team_id=?",
					new Object[]{teamID},
					(rs, n) -> rs.getString(1) + ' ' + rs.getString(2)
			);
			return jdbcTemplate.queryForObject(
					"SELECT t.id id, name, leader_name, score, start_time, " +
							"a.login login, a.pass pass FROM teams AS t " +
							"JOIN auth AS a ON t.id = a.team_id WHERE t.id=?",
					new Object[]{teamID},
					new TeamInfo.Mapper(members)
			);

		} catch (EmptyResultDataAccessException e) {
			throw new TeamDoesNotExist(e, teamID);
		}
	}

	public TeamInfo getTeamForPlayer(final Integer teamID) {
		return getTeamForModerator(teamID).eraseLoginPass();
	}

}
