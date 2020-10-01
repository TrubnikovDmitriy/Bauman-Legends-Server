package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TaskModel
import legends.models.TaskType

data class TaskView (
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("task_name") val taskName: String,
        @JsonProperty("html") val html: String,
        @JsonProperty("task_type") val taskType: TaskType,
        @JsonProperty("duration") val duration: Long?,
        @JsonProperty("points") val points: Int,
        @JsonProperty("answers") val answers: List<String>,
        @JsonProperty("capacity") val capacity: Int,
        @JsonProperty("skip") val skip: Boolean,
        @JsonProperty("max_attempts") val maxAttempts: Int?
) {
    constructor(task: TaskModel) : this(
            taskId = task.taskId,
            taskName = task.taskName,
            html = task.html,
            taskType = task.taskType,
            duration = task.duration?.let { it / 60 }, // sec -> min
            points = task.points,
            answers = task.answers,
            skip = task.skipPossible,
            capacity = task.capacity,
            maxAttempts = task.maxAttempts
    )
}

fun List<TaskModel>.toView(): List<TaskView>  = map { TaskView(it) }
