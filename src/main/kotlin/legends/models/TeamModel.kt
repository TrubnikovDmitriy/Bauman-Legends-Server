package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class TeamModel(
        val teamId: Long,
        val teamName: String,
        val leaderId: Long,
        val score: Int,
        val money: Int,
        val inviteCode: String,
        val size: Int
) {
    class Mapper : RowMapper<TeamModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): TeamModel? {
            return TeamModel(
                    teamId = rs.getLong("team_id"),
                    teamName = rs.getString("team_name"),
                    leaderId = rs.getLong("leader_id"),
                    score = rs.getInt("score"),
                    money = rs.getInt("money"),
                    inviteCode = rs.getString("invite_code"),
                    size = rs.getInt("size")
            )
        }
    }
}