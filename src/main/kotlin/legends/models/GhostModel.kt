package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class GhostModel(
        val ghostId: Long,
        val history: String
) {
    class Mapper : RowMapper<GhostModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): GhostModel? {
            return GhostModel(
                    ghostId = rs.getLong("ghost_id"),
                    history = rs.getString("history")
            )
        }
    }
}