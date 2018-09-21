package legends.dao;

import legends.models.TeamType;
import legends.responseviews.Team;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public class AuthDAO {

	private final JdbcTemplate jdbcTemplate;

	public AuthDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Nullable
	public Team getUser(@Nullable String login, @Nullable String pass) {
		if (login == null || pass == null) return null;
		try {
			return jdbcTemplate.queryForObject(
					"SELECT a.team_id AS team_id, t.name AS team_name, a.type AS team_type " +
							"FROM auth AS a JOIN teams AS t ON a.team_id = t.id " +
							"WHERE a.login=? AND a.pass=?",
					new Object[]{login, pass},
					(rs, i) -> new Team(
							rs.getString("team_name"),
							rs.getInt("team_id"),
							TeamType.values()[rs.getInt("team_type")]
					)
			);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
