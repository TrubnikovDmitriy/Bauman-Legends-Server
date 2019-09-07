package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.TeamModel

data class TeamView(
        @JsonProperty("team_id") val teamId: Long,
        @JsonProperty("team_name") val teamName: String,
        @JsonProperty("leader_id") val leaderId: Long,
        @JsonProperty("score") val score: Int,
        @JsonProperty("invite_code") val inviteCode: String?,
        @JsonProperty("size") val size: Int
) {
    constructor(userId: Long, team: TeamModel) : this(
            teamId = team.teamId,
            teamName = team.teamName,
            leaderId = team.leaderId,
            score = team.score,
            size = team.size,
            // Пригласительный код виден только капитану команды
            inviteCode = team.inviteCode.takeIf { userId == team.leaderId }
    )

    constructor(team: TeamModel) : this(
            teamId = team.teamId,
            teamName = team.teamName,
            leaderId = team.leaderId,
            score = team.score,
            size = team.size,
            inviteCode = null
    )
}

fun List<TeamModel>.toView(): List<TeamView>  = map { TeamView(it) }
