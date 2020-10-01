package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TeamState
import legends.models.TeamStatus

data class TeamStateView (
        @JsonProperty("status") val status: TeamStatus,
        @JsonProperty("task") val quest: QuestView?,
        @JsonProperty("text") val text: String?,
        @JsonProperty("attempts_count") val attemptsCount: Int?
) {
    constructor(state: TeamState) : this(
            status = state.status,
            quest = state.quest?.let { QuestView(it) },
            text = state.text,
            attemptsCount = state.attempts
    )
}
