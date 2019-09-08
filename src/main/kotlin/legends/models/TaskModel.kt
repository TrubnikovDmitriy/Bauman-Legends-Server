package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class TaskModel(
        val taskId: Long,
        val taskName: String,
        val html: String,
        val imagePath: String?,
        val taskType: TaskType,
        val duration: Int?,
        val points: Int,
        val answers: List<String>,
        val capacity: Int,
        val skipPossible: Boolean
) {
    @Suppress("UNCHECKED_CAST")
    class Mapper : RowMapper<TaskModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): TaskModel? {
            return TaskModel(
                    taskId = rs.getLong("task_id"),
                    taskName = rs.getString("task_name"),
                    html = rs.getString("html"),
                    imagePath = rs.getString("img_path").takeUnless { rs.wasNull() },
                    taskType = TaskType.valueOfSafety(rs.getString("task_type")),
                    duration = rs.getInt("duration").takeUnless { rs.wasNull() },
                    points = rs.getInt("points"),
                    answers = (rs.getArray("answers").array as Array<String>).asList(),
                    skipPossible = rs.getBoolean("skip_possible"),
                    capacity = rs.getInt("capacity")
            )
        }
    }
    ;
}