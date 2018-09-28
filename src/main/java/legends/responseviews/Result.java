package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

	@JsonProperty("is_correct")
	private final Boolean correct;

	public Result(Boolean correct) {
		this.correct = correct;
	}
}
