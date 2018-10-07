package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TeamType;

public class TeamAuth {

	@JsonProperty private String teamName;
	@JsonProperty private Integer teamID;
	@JsonProperty private TeamType teamType;

	public TeamAuth() { }

	public TeamAuth(String teamName, Integer teamID, TeamType teamType) {
		this.teamName = teamName;
		this.teamID = teamID;
		this.teamType = teamType;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getTeamID() {
		return teamID;
	}

	public void setTeamID(Integer teamID) {
		this.teamID = teamID;
	}

	public TeamType getTeamType() {
		return teamType;
	}

	public void setTeamType(TeamType teamType) {
		this.teamType = teamType;
	}

	@Override
	public String toString() {
		return "teamName='" + teamName +
				"', teamID=" + teamID +
				", teamType=" + teamType;
	}
}
