package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TeamForTable;

import java.util.List;

public class TableOfTeams {

	@JsonProperty
	private final List<TeamForTable> teams;

	public TableOfTeams(List<TeamForTable> teams) {
		this.teams = teams;
	}
}
