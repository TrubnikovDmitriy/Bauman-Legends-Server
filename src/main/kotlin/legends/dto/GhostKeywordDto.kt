package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.HintModel

data class GhostKeywordDto(
        @JsonProperty("keyword") val keyword: String
)