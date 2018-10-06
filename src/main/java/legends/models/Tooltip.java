package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Tooltip {

	@JsonProperty
	private Integer number;

	@JsonProperty
	private String message;

	public Tooltip(Integer number, String message) {
		this.number = number;
		this.message = message;
	}
}
