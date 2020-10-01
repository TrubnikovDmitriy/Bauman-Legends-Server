package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.BaumanModel

data class BaumanFragmentsView(
        @JsonProperty("fragment_ids") val fragmentId: List<Int>
) {
    constructor(model: BaumanModel) : this(model.fragments)
}