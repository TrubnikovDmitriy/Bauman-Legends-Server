package legends.dao;

import legends.models.TeamType;
import legends.requestviews.FullTeam;
import legends.requestviews.Player;
import legends.responseviews.TeamAuth;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AuthDAO {

	@NonNull private final DataSource dataSource;
	@NonNull private final JdbcTemplate jdbcTemplate;
	@NonNull private final SimpleJdbcInsert jdbcInsert;

	public AuthDAO(@NonNull DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource)
				.withTableName("teams")
				.usingGeneratedKeyColumns("id");
	}

	@Nullable
	public TeamAuth getUser(@Nullable String login, @Nullable String pass) {
		if (login == null || pass == null) return null;
		try {
			return jdbcTemplate.queryForObject(
					"SELECT a.team_id AS team_id, t.name AS team_name, a.type AS team_type " +
							"FROM auth AS a JOIN teams AS t ON a.team_id = t.id " +
							"WHERE a.login=? AND a.pass=?",
					new Object[] { login, pass },
					(rs, i) -> new TeamAuth(
							rs.getString("team_name"),
							rs.getInt("team_id"),
							TeamType.valueOf(rs.getString("team_type"))
					)
			);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void signUpTeam(@NonNull FullTeam team) {

		final Player leader = team.getLeader();
		final List<Player> members = team.getMembers();
		members.add(leader);

		// Forming the team and retieve its ID
		final Map<String, Object> parameters = new HashMap<>(2);
		parameters.put("name", team.getName());
		parameters.put("leader_name", leader.getFirstName() + ' ' + leader.getSecondName());
		parameters.put("score", 0);
		final Array emptyArray = getEmptySQLArray();
		parameters.put("final_tasks_arr", emptyArray);
		parameters.put("pilot_tasks_arr", emptyArray);

		final int teamID = jdbcInsert.executeAndReturnKey(parameters).intValue();


		// Inserting all members of team
		jdbcTemplate.batchUpdate(
				"INSERT INTO players(first_name, second_name, team_id) VALUES (?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int rowNumber)
							throws SQLException {
						ps.setString(1, members.get(rowNumber).getFirstName());
						ps.setString(2, members.get(rowNumber).getSecondName());
						ps.setInt(3, teamID);
					}

					@Override
					public int getBatchSize() {
						return members.size();
					}
				}
		);

		// Creating account for team
		jdbcTemplate.update(
				"INSERT INTO auth(team_id, login, pass, type) VALUES(?, ?, DEFAULT, DEFAULT)",
				teamID, team.getName() + '-' + teamID
		);
	}

	private Array getEmptySQLArray() {
		try(final Connection connection = dataSource.getConnection()) {
			return connection.createArrayOf("integer", Collections.EMPTY_LIST.toArray());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
