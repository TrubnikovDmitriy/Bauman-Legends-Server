package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessage {

	@JsonProperty private final String message;

	public ErrorMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
