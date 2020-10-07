package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class FactModel(
        val taskId: Long,
        val text: String
) {
    class Mapper : RowMapper<FactModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): FactModel? {
            return FactModel(
                    taskId = rs.getLong("task_id"),
                    text = rs.getString("fact")
            )
        }
    }
}