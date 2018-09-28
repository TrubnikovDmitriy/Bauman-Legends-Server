package legends.dao;

import legends.exceptions.TaskIsNotExist;
import legends.exceptions.TeamDoesNotExist;
import legends.models.Router;
import legends.models.TeamForTable;
import legends.models.Trail;
import legends.requestviews.Answer;
import legends.responseviews.TeamInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
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

	public List<Router> getRouters(final boolean fullList) {
		final HashMap<Integer, List<Trail>> map = new HashMap<>();
		if (fullList) {
			jdbcTemplate.query(
					"SELECT task_id, team_id, start_time, finish_time, success " +
							"FROM current_tasks WHERE type='FINAL'",
					new HashMapper(map)
			);
			return jdbcTemplate.query(
					"SELECT teams.id, name, COUNT(players.id) AS players_count, final_tasks_arr, fails_count, start_time " +
							"FROM teams JOIN players ON teams.id=players.team_id GROUP BY teams.id",
					new Router.Mapper(map)
			);
		} else {
			jdbcTemplate.query(
					"SELECT task_id, team_id, ct.start_time, ct.finish_time, success " +
							"FROM current_tasks ct JOIN teams t ON ct.team_id = t.id " +
							"WHERE type='FINAL' AND t.started=TRUE AND finished=FALSE",
					new HashMapper(map)
			);
			return jdbcTemplate.query(
					"SELECT teams.id, name, COUNT(players.id) AS players_count, final_tasks_arr, fails_count, start_time " +
							"FROM teams JOIN players ON teams.id=players.team_id " +
							"WHERE started=TRUE and finished=FALSE GROUP BY teams.id",
					new Router.Mapper(map)
			);
		}
	}

	private static class HashMapper implements RowMapper<Object> {
		final RowMapper<Trail> mapper = new Trail.Mapper();
		private final HashMap<Integer, List<Trail>> map;

		HashMapper(@NotNull HashMap<Integer, List<Trail>> map) {
			this.map = map;
		}

		@Nullable
		@Override
		public Object mapRow(ResultSet rs, int i) throws SQLException {
			final Integer teamID = rs.getInt("team_id");
			final List<Trail> trails = map.getOrDefault(teamID, new LinkedList<>());
			trails.add(mapper.mapRow(rs, i));
			map.put(teamID, trails);
			return null;
		}
	}

	public void validateAnswer(final Answer answer) {
		try {
			jdbcTemplate.queryForObject(
					"SELECT id FROM current_tasks WHERE " +
							"team_id=? AND task_id=? AND type=? AND success IS NULL;",
					new Object[] {
							answer.getTeamID(),
							answer.getTaskID(),
							answer.getTaskType().name()
					},
					Integer.class
			);

		} catch (EmptyResultDataAccessException ignore) {
			throw new TaskIsNotExist();
		}
	}
}
