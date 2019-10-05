package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.GameStage

data class GameStateUpdate(
        @JsonProperty(required = true, value = "status") val stage: GameStage,
        @JsonProperty(required = true, value = "secret") val secret: String
)