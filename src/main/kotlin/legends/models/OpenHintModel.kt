package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class OpenHintModel(
        val isOpen: Boolean,
        val hint: HintModel
) {
    class Mapper : RowMapper<OpenHintModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): OpenHintModel? {
            val hint = HintModel.Mapper().mapRow(rs, rowNum) ?: return null
            val isOpen = rs.run {
                getLong("team_id")
                !rs.wasNull()
            }

            return OpenHintModel(
                    isOpen = isOpen,
                    hint = hint
            )
        }
    }
}