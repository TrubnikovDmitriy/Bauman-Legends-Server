package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecretKey {

	@JsonProperty(required = true, value = "team_id")
	private Integer teamID;

	@JsonProperty(required = true, value = "secret_key")
	private String key;

	public SecretKey(Integer teamID, String key) {
		this.teamID = teamID;
		this.key = key;
	}

	public Integer getTeamID() {
		return teamID;
	}

	public void setTeamID(Integer teamID) {
		this.teamID = teamID;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
