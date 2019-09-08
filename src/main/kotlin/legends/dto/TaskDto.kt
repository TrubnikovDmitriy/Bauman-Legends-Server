package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TaskType

data class TaskDto (
        @JsonProperty(required = false, value = "task_id") val taskId: Long?,
        @JsonProperty(required = true, value = "task_name") val taskName: String,
        @JsonProperty(required = true, value = "html") val html: String,
        @JsonProperty(required = false, value = "img_path") val imagePath: String?,
        @JsonProperty(required = true, value = "task_type") val taskType: TaskType,
        @JsonProperty(required = false, value = "duration") val duration: Int?,
        @JsonProperty(required = true, value = "points") val points: Int,
        @JsonProperty(required = true, value = "answers") val answers: List<String>,
        @JsonProperty(required = true, value = "capacity") val capacity: Int
)
