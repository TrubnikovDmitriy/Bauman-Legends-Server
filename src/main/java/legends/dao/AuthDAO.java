package legends.dao;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import legends.models.TeamType;
import legends.requestviews.FullTeam;
import legends.requestviews.Player;
import legends.responseviews.TeamInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuthDAO {

	private final JdbcTemplate jdbcTemplate;

	public AuthDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Nullable
	public TeamInfo getUser(@Nullable String login, @Nullable String pass) {
		if (login == null || pass == null) return null;
		try {
			return jdbcTemplate.queryForObject(
					"SELECT a.team_id AS team_id, t.name AS team_name, a.type AS team_type " +
							"FROM auth AS a JOIN teams AS t ON a.team_id = t.id " +
							"WHERE a.login=? AND a.pass=?",
					new Object[] { login, pass },
					(rs, i) -> new TeamInfo(
							rs.getString("team_name"),
							rs.getInt("team_id"),
							TeamType.values()[rs.getInt("team_type")]
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

		// Forming the team
		final Integer teamID = jdbcTemplate.queryForObject(
				"INSERT INTO teams(name, leader_name) VALUES (?, ?)",
				new Object[] { team.getName(), leader.getFirstName() + leader.getSecondName() },
				Integer.class
		);

		// Inserting all members of team
		jdbcTemplate.batchUpdate(
				"INSERT INTO players(first_name, second_name, team_id) VALUES (?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int rowNumber)
							throws SQLException {
						ps.setString(1, members.get(rowNumber).getFirstName());
						ps.setString(2, members.get(rowNumber).getSecondName());
						ps.setInt(4, teamID);
					}

					@Override
					public int getBatchSize() {
						return members.size();
					}
				}
		);

		// Creating account for team
		final String password = generatePassword(team);
		jdbcTemplate.update(
				"INSERT INTO auth(team_id, pass, login) VALUES(?, ?, ?)",
				teamID, password, team.getName() + '-' + teamID
		);
	}


	private static UUIDGenerator passwordGenerator = new UUIDGenerator();
	private static String generatePassword(Object seed) {
		return passwordGenerator.generateId(seed).toString().split("-")[0];
	}
}
