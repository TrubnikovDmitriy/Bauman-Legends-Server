package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.HintModel

data class WitnessKeywordDto(
        @JsonProperty("keyword") val keyword: String
)

data class DecisionDto(
        @JsonProperty("freedom") val freedom: Boolean
)