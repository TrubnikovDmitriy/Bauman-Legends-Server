package legends.models;

public class TaskStatus {

	private final int taskCount;
	private final int totalTaskCount;
	private final int currentTaskID;

	public TaskStatus(int taskCount, int totalTaskCount, int currentTaskID) {
		this.taskCount = taskCount;
		this.totalTaskCount = totalTaskCount;
		this.currentTaskID = currentTaskID;
	}

	public boolean isFinished() {
		return taskCount == totalTaskCount;
	}

	public int getCurrentTaskID() {
		return currentTaskID;
	}
}
