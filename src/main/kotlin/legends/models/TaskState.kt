package legends.models

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class TaskState(
        val taskId: Long,
        val taskType: TaskType,
        val capacity: Int,
        val load: Int,
        val hints: Long
) {

    val loadFactor: Float = load.toFloat() / capacity

    class Mapper : RowMapper<TaskState> {
        override fun mapRow(rs: ResultSet, rowNum: Int): TaskState? {
            return TaskState(
                    taskId = rs.getLong("task_id"),
                    taskType = TaskType.valueOfSafety(rs.getString("task_type")),
                    capacity = rs.getInt("capacity"),
                    load = rs.getInt("load"),
                    hints = rs.getLong("hints")
            )
        }
    }
}