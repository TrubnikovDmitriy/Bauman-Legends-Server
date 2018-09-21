package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TeamType;

public class Team {

	@JsonProperty private String teamName;
	@JsonProperty private Integer teamNumber;
	@JsonProperty private TeamType teamType;

	public Team() { }

	public Team(String teamName, Integer teamNumber, TeamType teamType) {
		this.teamName = teamName;
		this.teamNumber = teamNumber;
		this.teamType = teamType;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(Integer teamNumber) {
		this.teamNumber = teamNumber;
	}

	public TeamType getTeamType() {
		return teamType;
	}

	public void setTeamType(TeamType teamType) {
		this.teamType = teamType;
	}
}
