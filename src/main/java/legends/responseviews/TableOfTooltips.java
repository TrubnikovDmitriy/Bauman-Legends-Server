package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.Tooltip;

import java.util.List;

public class TableOfTooltips {

	@JsonProperty
	private List<Tooltip> tooltips;

	public TableOfTooltips(List<Tooltip> tooltips) {
		this.tooltips = tooltips;
	}
}
