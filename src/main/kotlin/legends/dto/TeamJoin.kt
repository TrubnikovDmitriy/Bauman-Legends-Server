package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TeamJoin (
        @JsonProperty(required = true, value = "team_id") val teamId: Long,
        @JsonProperty(required = true, value = "invite_code") val inviteCode: String
)