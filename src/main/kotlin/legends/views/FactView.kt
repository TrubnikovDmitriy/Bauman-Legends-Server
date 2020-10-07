package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.FactModel

data class FactView(
        @JsonProperty("task_id") val taskId: Long?,
        @JsonProperty("fact") val text: String?
) {
    constructor(fact: FactModel?) : this(fact?.taskId, fact?.text)
}