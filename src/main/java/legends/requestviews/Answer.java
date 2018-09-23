package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {

	@JsonProperty("team_id")
	private Integer teamID;

	@JsonProperty("task_id")
	private Integer taskID;

	@JsonProperty("answer")
	private String answer;

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
}
