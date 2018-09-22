package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TeamInfo {

	@JsonProperty("team_id")
	private Integer teamID;

	@JsonProperty("team_name")
	private String teamName;

	@JsonProperty("leader_name")
	private String leaderName;

	@JsonProperty("members_names")
	private List<String> membersNames;

	@JsonProperty("start_time")
	private Integer startTime;

	@JsonProperty
	private Integer score;

	@JsonProperty
	private String login;

	@JsonProperty
	private String password;

	public void eraseLoginPass() {
		login = password = null;
	}

	public static final class Mapper implements RowMapper<TeamInfo> {

		private final List<String> members;

		public Mapper(List<String> members) {
			this.members = members;
		}

		@Override
		public TeamInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			final TeamInfo team = new TeamInfo();

			team.teamID = rs.getInt("id");
			team.teamName= rs.getString("name");
			team.leaderName = rs.getString("leader_name");
			team.score = rs.getInt("score");
			team.startTime = rs.getInt("start_time");
			team.login = rs.getString("login");
			team.password = rs.getString("pass");

			this.members.remove(team.leaderName);
			team.membersNames = this.members;

			return team;
		}
	}

}
