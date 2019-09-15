package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.AnswerModel

data class AnswerDto(
        @JsonProperty(required = true, value = "team_id") val teamId: Long,
        @JsonProperty(required = true, value = "task_id") val taskId: Long,
        @JsonProperty(required = true, value = "answer") val answer: String
) {
    fun convert(): AnswerModel {
        return AnswerModel(
                teamId = teamId,
                taskId = taskId,
                answer = answer
        )
    }
}