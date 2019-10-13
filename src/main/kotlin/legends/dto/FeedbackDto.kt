package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.FeedbackModel

data class FeedbackDto(
        @JsonProperty(required = true, value = "legends_mark") val legendsMark: Int,
        @JsonProperty(required = true, value = "pilot_mark") val pilotMark: Int,
        @JsonProperty(required = true, value = "final_mark") val finalMark: Int,
        @JsonProperty(required = true, value = "site_mark") val siteMark: Int,
        @JsonProperty(required = true, value = "task_mark") val taskMark: Int,
        @JsonProperty(required = true, value = "ghost_mark") val ghostMark: Int,
        @JsonProperty(required = false, value = "best_task") val bestTask: String?,
        @JsonProperty(required = false, value = "worst_task") val worstTask: String?,
        @JsonProperty(required = false, value = "from") val from: String?,
        @JsonProperty(required = false, value = "message") val message: String?
) {
    fun convert(userId: Long): FeedbackModel {
        return FeedbackModel(
                userId = userId,
                pilotMark = pilotMark,
                finalMark = finalMark,
                legendsMark = legendsMark,
                siteMark = siteMark,
                taskMark = taskMark,
                ghostMark = ghostMark,
                bestTask = bestTask?.trim() ?: "",
                worstTask = worstTask?.trim() ?: "",
                from = from?.trim() ?: "",
                message = message?.trim() ?: ""
        )
    }
}