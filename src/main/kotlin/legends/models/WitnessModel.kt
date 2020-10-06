package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class WitnessModel(
        val ghostId: Long
) {
    class Mapper : RowMapper<WitnessModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): WitnessModel? {
            return WitnessModel(
                    ghostId = rs.getLong("ghost_id")
            )
        }
    }
}