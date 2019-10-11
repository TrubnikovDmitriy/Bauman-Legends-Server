package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.HintModel
import legends.models.OpenHintModel

data class GroupHintsView(
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("hints") val hints: List<HintView>
)

data class HintView(
        @JsonProperty("hint_id") val hintId: Long,
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("html") val html: String?,
        @JsonProperty("cost") val cost: Int
) {
    constructor(hint: HintModel) : this(
            hintId = hint.hintId,
            taskId = hint.taskId,
            html = hint.html,
            cost = hint.cost
    )
}


fun List<HintModel>.toGroupView(): List<GroupHintsView> {
    return this
            .map { HintView(it) }
            .groupBy { it.taskId }
            .map { GroupHintsView(it.key, it.value) }
}

fun List<OpenHintModel>.toPlayerView(): List<HintView> = map { openHint ->
    HintView(
            hintId = openHint.hint.hintId,
            taskId = openHint.hint.taskId,
            html = null,
            cost = openHint.hint.cost
    )
}

fun List<HintModel>.toView(): List<HintView> = map { HintView(it) }

