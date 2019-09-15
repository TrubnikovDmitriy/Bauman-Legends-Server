package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.GameStatus

data class GameStateUpdate(
        @JsonProperty(required = true, value = "status") val status: GameStatus,
        @JsonProperty(required = true, value = "secret") val secret: String
)