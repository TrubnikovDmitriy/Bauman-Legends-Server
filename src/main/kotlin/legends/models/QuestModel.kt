package legends.models

import legends.utils.AnswersCoder.decodeAnswer
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class QuestModel(
        // Original result
        val teamId: Long,
        val taskId: Long,
        val startTime: Long, // seconds
        val finishTime: Long?, // seconds
        val answer: String?,
        val status: QuestStatus,
        // Join with task
        val taskName: String,
        val html: String,
        val taskType: TaskType,
        val duration: Long?, // seconds
        val points: Int,
        val answers: List<String>,
        val capacity: Int,
        val skipPossible: Boolean,
        val maxAttempts: Int?
) {
    @Suppress("UNCHECKED_CAST")
    class Mapper : RowMapper<QuestModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): QuestModel? {
            return QuestModel(
                    teamId = rs.getLong("team_id"),
                    taskId = rs.getLong("task_id"),
                    taskName = rs.getString("task_name"),
                    startTime = rs.getLong("start_time"),
                    finishTime = rs.getLong("finish_time").takeUnless { rs.wasNull() },
                    status = QuestStatus.valueOfSafety(rs.getString("status")),
                    html = rs.getString("html"),
                    taskType = TaskType.valueOfSafety(rs.getString("task_type")),
                    duration = rs.getLong("duration").takeUnless { rs.wasNull() },
                    points = rs.getInt("points"),
                    skipPossible = rs.getBoolean("skip_possible"),
                    answer = rs.getString("answer").takeUnless { rs.wasNull() },
                    answers = (rs.getArray("answers").array as Array<String>).map { it.decodeAnswer() },
                    capacity = rs.getInt("capacity"),
                    maxAttempts = rs.getInt("max_attempts").takeUnless { rs.wasNull() }
            )
        }
    }
}