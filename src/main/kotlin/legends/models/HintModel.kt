package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class HintModel(
        val hintId: Long,
        val taskId: Long,
        val html: String,
        val cost: Int
) {
    class Mapper : RowMapper<HintModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): HintModel? {
            return HintModel(
                    hintId = rs.getLong("hint_id"),
                    taskId = rs.getLong("task_id"),
                    html = rs.getString("html"),
                    cost = rs.getInt("cost")
            )
        }
    }
}