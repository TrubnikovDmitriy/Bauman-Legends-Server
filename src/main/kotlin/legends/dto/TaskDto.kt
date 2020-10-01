package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TaskModel
import legends.models.TaskType
import java.util.concurrent.TimeUnit

data class TaskDto (
        @JsonProperty(required = false, value = "task_id") val taskId: Long?,
        @JsonProperty(required = true, value = "task_name") val taskName: String,
        @JsonProperty(required = true, value = "task_type") val taskType: TaskType,
        @JsonProperty(required = false, value = "duration") val duration: Long?, // minutes
        @JsonProperty(required = true, value = "points") val points: Int,
        @JsonProperty(required = true, value = "answers") val answers: List<String>,
        @JsonProperty(required = true, value = "capacity") val capacity: Int,
        @JsonProperty(required = false, value = "img_path") val imagePath: String?,
        @JsonProperty(required = true, value = "html") val html: String,
        @JsonProperty(required = false, value = "max_attempts") val maxAttempts: Int?
) {
    fun convert(taskId: Long): TaskModel {
        return TaskModel(
                taskId = taskId,
                taskName = taskName.trim(),
                html = html,
                imagePath = imagePath,
                taskType = taskType,
                duration = duration?.let { it * 60 }, // min -> sec
                points = points,
                answers = answers.map { it.trim() },
                capacity = capacity,
                skipPossible = (taskType == TaskType.LOGIC),
                maxAttempts = maxAttempts
        )
    }
}
