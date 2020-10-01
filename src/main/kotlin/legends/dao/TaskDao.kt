package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.TaskIsNotExists
import legends.models.TaskModel
import legends.models.TaskType
import legends.utils.SqlUtils.convertToSqlArray
import legends.utils.SqlUtils.setNullable
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement
import javax.sql.DataSource

@Repository
class TaskDao(private val dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(TaskDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun getTaskById(taskId: Long): TaskModel? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM tasks WHERE task_id=?",
                    arrayOf(taskId),
                    TaskModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getTaskOrThrow(taskId: Long): TaskModel {
        return getTaskById(taskId) ?: throw TaskIsNotExists(taskId)
    }

    /**
     * Use `JdbcTemplate` for INSERT instead of `SimpleJdbcInsert` due to the problem with enum values
     */
    fun insertTask(task: TaskModel): Long {
        val sqlArray = convertToSqlArray(task.answers, dataSource)
        val keyHolder = GeneratedKeyHolder()

        val insertStatement = PreparedStatementCreator { connection ->
            connection.prepareStatement(
                    """
                    INSERT INTO tasks 
                    (task_name, html, img_path, task_type, duration, points, answers, skip_possible, capacity, max_attempts) 
                    VALUES (?, ?, ?, LOWER(?)::task_type, ?, ?, ?, ?, ?, ?) 
                    RETURNING task_id
                    """,
                    Statement.RETURN_GENERATED_KEYS
            ).apply {
                setString(1, task.taskName)
                setString(2, task.html)
                setNullable(3, task.imagePath, this)
                setString(4, task.taskType.name)
                setNullable(5, task.duration, this)
                setInt(6, task.points)
                setArray(7, sqlArray)
                setBoolean(8, task.skipPossible)
                setInt(9, task.capacity)
                setNullable(10, task.maxAttempts, this)
            }
        }

        try {
            jdbcTemplate.update(insertStatement, keyHolder)
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Задание с названием \"${task.taskName}\" уже существует." }
        }

        return requireNotNull(keyHolder.key).toLong()
    }

    fun updateTask(task: TaskModel) {
        try {
            val sqlArray = convertToSqlArray(task.answers, dataSource)
            val affectedRows = jdbcTemplate.update(
                    """
                    UPDATE tasks 
                    SET (task_name, html, img_path, task_type, duration, points, answers, capacity, max_attempts)=(?, ?, ?, LOWER(?)::task_type, ?, ?, ?, ?, ?) 
                    WHERE task_id=?
                    """,
                    task.taskName, task.html, task.imagePath, task.taskType.name, task.duration, task.points, sqlArray, task.capacity, task.maxAttempts, task.taskId
            )

            if (affectedRows == 0) {
                throw TaskIsNotExists(task.taskId)
            }

            if (affectedRows != 1) {
                logger.error("Fail to update task, task=[$task], affectedRows=[$affectedRows]")
                throw BadRequestException { "Не удалось обновить задание \"${task.taskName}\" под номером [${task.taskId}]." }
            }

        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Задание с названием \"${task.taskName}\" уже существует." }
        }
    }

    fun getTasksByType(taskType: TaskType): List<TaskModel> {
        return jdbcTemplate.query(
                "SELECT * FROM tasks WHERE task_type=LOWER(?)::task_type",
                arrayOf(taskType.name),
                TaskModel.Mapper()
        )
    }

    fun getAllTasks(): List<TaskModel> {
        return jdbcTemplate.query(
                "SELECT * FROM tasks",
                TaskModel.Mapper()
        )
    }

    fun deleteTask(taskId: Long) {
        try {
            val affectedRows = jdbcTemplate.update("DELETE FROM tasks WHERE task_id=?", taskId)

            if (affectedRows == 0) {
                throw TaskIsNotExists(taskId)
            }
            if (affectedRows != 1) {
                logger.error("Fail to delete task, taskId=[$taskId], affectedRows=[$affectedRows]")
            }
            return

        } catch (e: DataIntegrityViolationException) {
            logger.warn("Fail to delete task with taskId=[$taskId]", e)
            throw BadRequestException {
                "Нельзя удалять задания, которые уже проходились участниками."
            }
        }
    }
}
