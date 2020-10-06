package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.WitnessModel
import legends.services.WitnessService

data class WitnessView(
        @JsonProperty("witness_id") val ghostId: Long,
        @JsonProperty("open") val open: Boolean
) {
    constructor(witness: WitnessModel) : this(witness.ghostId, true)
}

fun List<WitnessModel>.toView(): List<WitnessView> {
    val witnessCount = WitnessService.ALL_WITNESS_COUNT
    val list = ArrayList<WitnessView>(witnessCount)
    for (ghostIndex in 1L..witnessCount) {
        val ghost = this.firstOrNull { it.ghostId == ghostIndex }
        list.add(WitnessView(ghostIndex, ghost != null))
    }
    return list
}