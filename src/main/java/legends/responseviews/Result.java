package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskType;

public class Result {

	@JsonProperty("is_correct")
	private final Boolean correct;

	@JsonProperty("task_type")
	private final TaskType type;

	@JsonProperty("tooltip")
	private final String tooltip;

	public Result(Boolean correct, TaskType type, String tooltip) {
		this.correct = correct;
		this.type = type;
		this.tooltip = tooltip;
	}

	@JsonIgnore
	public Boolean isCorrect() {
		return correct;
	}
}
