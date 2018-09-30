package legends.models;

public class NewTask {

	public final int teamID;
	public final int taskID;
	public final int duration;

	public NewTask(int teamID, int taskID, int duration) {
		this.teamID = teamID;
		this.taskID = taskID;
		this.duration = duration;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NewTask)) return false;

		final NewTask taskTimer = (NewTask) o;

		if (teamID != taskTimer.teamID) return false;
		if (taskID != taskTimer.taskID) return false;
		return duration == taskTimer.duration;
	}

	@Override
	public int hashCode() {
		int result = teamID;
		result = 31 * result + taskID;
		result = 31 * result + duration;
		return result;
	}
}
