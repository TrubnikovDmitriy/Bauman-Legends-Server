package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TeamSignUp (
        @JsonProperty(required = true, value = "team_name") val teamName: String
)