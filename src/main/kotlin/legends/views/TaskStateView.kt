package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TaskModel
import legends.models.TaskState
import legends.models.TaskType

data class TaskStateView (
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("task_name") val taskName: String,
        @JsonProperty("task_type") val taskType: TaskType,
        @JsonProperty("capacity") val capacity: Int,
        @JsonProperty("load") val load: Int,
        @JsonProperty("hints") val hints: Long
) {
    constructor(task: TaskState) : this(
            taskId = task.taskId,
            taskName = task.taskName,
            taskType = task.taskType,
            load = task.load,
            hints = task.hints,
            capacity = task.capacity
    )
}

fun List<TaskState>.toView(): List<TaskStateView>  = map { TaskStateView(it) }
