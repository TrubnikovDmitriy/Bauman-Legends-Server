package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskType;

public class Answer {

	@JsonProperty(value = "team_id", required = true)
	private Integer teamID;

	@JsonProperty(value = "task_id", required = true)
	private Integer taskID;

	@JsonProperty("answer")
	private String answer;

	@JsonProperty(value = "task_type", required = true)
	private TaskType taskType;

	public Integer getTeamID() {
		return teamID;
	}

	public void setTeamID(Integer teamID) {
		this.teamID = teamID;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@Override
	public String toString() {
		return "teamID=" + teamID + ", taskID=" + taskID +
				", answer='" + answer + "', taskType=" + taskType;
	}
}
