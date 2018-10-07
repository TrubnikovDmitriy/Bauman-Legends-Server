package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.Tooltip;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
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

	@JsonProperty("tooltips_table")
	private List<Tooltip> tooltips;

	@JsonProperty
	private Integer score;

	@JsonProperty
	private String login;

	@JsonProperty
	private String password;


	public TeamInfo eraseLoginPass() {
		login = password = null;
		return this;
	}

	public void setTooltips(List<Tooltip> tooltips) {
		this.tooltips = tooltips;
	}


	public static final class Mapper implements RowMapper<TeamInfo> {

		private final List<String> members;
		private final List<Tooltip> tooltips;

		public Mapper(@NonNull List<String> members,
		              @NotNull List<Tooltip> tooltips) {
			this.members = members;
			this.tooltips = tooltips;
		}

		@Override
		public TeamInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			final TeamInfo team = new TeamInfo();

			team.teamID = rs.getInt("id");
			team.teamName= rs.getString("name");
			team.leaderName = rs.getString("leader_name");
			team.score = rs.getInt("score");
			team.login = rs.getString("login");
			team.password = rs.getString("pass");
			team.startTime = rs.getInt("start_time");
			if (rs.wasNull()) team.startTime = null;

			// Remove leader name from list of members' names
			this.members.remove(team.leaderName);
			team.membersNames = this.members;

			team.tooltips = tooltips;

			return team;
		}
	}


	@Override
	public String toString() {
		return "teamID=" + teamID +", teamName='" + teamName +
				", score=" + score + "', tooltips=" + tooltips;
	}
}
