package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FullTeam {

	@JsonProperty(value = "team_name", required = true) private String name;
	@JsonProperty(required = true) private Player leader;
	@JsonProperty(required = true) private List<Player> members;

	public FullTeam() { }

	public boolean isValid() {
		if (leader == null || !leader.isValid()) {
			return false;
		}
		for (final Player member : members) {
			if (!member.isValid()) return false;
		}
		return true;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player getLeader() {
		return leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
	}

	public List<Player> getMembers() {
		return members;
	}

	public void setMembers(List<Player> members) {
		this.members = members;
	}
}
