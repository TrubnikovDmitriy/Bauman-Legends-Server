package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TeamForTable;

import java.util.List;

public class Table {

	@JsonProperty
	private final List<TeamForTable> teams;

	public Table(List<TeamForTable> teams) {
		this.teams = teams;
	}
}
