package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.GhostModel

data class GhostView(
        @JsonProperty("ghost_id") val ghostId: Long,
        @JsonProperty("history") val history: String?
) {
    constructor(ghost: GhostModel) : this(
            ghostId = ghost.ghostId,
            history = ghost.history
    )
}

fun List<GhostModel>.toView(): List<GhostView> {
    val list = ArrayList<GhostView>(6)
    for (ghostIndex in 1..6L) {
        val ghost = firstOrNull { it.ghostId == ghostIndex }
        val ghostView = ghost?.let { GhostView(it) } ?: GhostView(ghostIndex, null)
        list.add(ghostView)
    }
    return list;
}
