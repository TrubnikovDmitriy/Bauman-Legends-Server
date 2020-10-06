package legends.views

import com.fasterxml.jackson.annotation.JsonProperty

data class DecisionView(
        @JsonProperty("text") val result: String
)