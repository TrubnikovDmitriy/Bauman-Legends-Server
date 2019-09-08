package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskTypeOld;

public class Result {

	@JsonProperty("is_correct")
	private final Boolean correct;

	@JsonProperty("task_type")
	private final TaskTypeOld type;

	@JsonProperty("tooltip")
	private final String tooltip;

	public Result(Boolean correct, TaskTypeOld type, String tooltip) {
		this.correct = correct;
		this.type = type;
		this.tooltip = tooltip;
	}

	@JsonIgnore
	public Boolean isCorrect() {
		return correct;
	}
}
