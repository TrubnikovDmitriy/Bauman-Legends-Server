package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.HintModel

data class HintDto(
        @JsonProperty("hint_id") val hintId: Long?,
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("html") val html: String,
        @JsonProperty("cost") val cost: Int
) {
    fun convert(id: Long): HintModel {
        return HintModel(
                hintId = id,
                taskId = taskId,
                html = html,
                cost = cost
        )
    }
}