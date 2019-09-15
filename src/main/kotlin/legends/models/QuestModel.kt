package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class QuestModel(
        // Original result
        val teamId: Long,
        val taskId: Long,
        val startTime: Long, // seconds
        val finishTime: Long?, // seconds
        val answer: String?,
        val status: TaskStatus,
        // Join with task
        val html: String,
        val taskType: TaskType,
        val duration: Long?, // seconds
        val points: Int,
        val answers: List<String>,
        val capacity: Int,
        val skipPossible: Boolean
) {
    @Suppress("UNCHECKED_CAST")
    class Mapper : RowMapper<QuestModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): QuestModel? {
            return QuestModel(
                    teamId = rs.getLong("team_id"),
                    taskId = rs.getLong("task_id"),
                    startTime = rs.getLong("start_time"),
                    finishTime = rs.getLong("finish_time").takeUnless { rs.wasNull() },
                    status = TaskStatus.valueOfSafety(rs.getString("status")),
                    html = rs.getString("html"),
                    taskType = TaskType.valueOfSafety(rs.getString("task_type")),
                    duration = rs.getLong("duration").takeUnless { rs.wasNull() },
                    points = rs.getInt("points"),
                    skipPossible = rs.getBoolean("skip_possible"),
                    answer = rs.getString("answer").takeUnless { rs.wasNull() },
                    answers = (rs.getArray("answers").array as Array<String>).asList(),
                    capacity = rs.getInt("capacity")
            )
        }
    }
}