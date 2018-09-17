package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Team {

	@JsonProperty // by default JSON field will have the same name
	private String teamName;
	@JsonProperty
	private Player teamLeader;


	public Team(String teamName, Player teamLeader) {
		this.teamName = teamName;
		this.teamLeader = teamLeader;
	}


	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Player getTeamLeader() {
		return teamLeader;
	}

	public void setTeamLeader(Player teamLeader) {
		this.teamLeader = teamLeader;
	}
}
