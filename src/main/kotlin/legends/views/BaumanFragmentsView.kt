package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.BaumanModel

data class BaumanFragmentsView(
        @JsonProperty("document_id") val documentId: Int,
        @JsonProperty("url") val url: String
) {
    constructor(model: BaumanModel) : this(model.documentId, model.url)
}

fun List<BaumanModel>.toView(): List<BaumanFragmentsView>  = map { BaumanFragmentsView(it) }