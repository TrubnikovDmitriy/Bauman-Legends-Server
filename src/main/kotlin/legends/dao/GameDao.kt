package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.QuestIsNotExists
import legends.models.*
import legends.utils.TimeUtils
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.RowMapperResultSetExtractor
import org.springframework.stereotype.Repository
import java.sql.Statement
import java.util.concurrent.TimeUnit.SECONDS
import javax.sql.DataSource

@Repository
class GameDao(dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(GameDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun startTask(teamId: Long, taskId: Long) {
        val timestamp = TimeUtils.currentTime(SECONDS)
        val insertStatement = PreparedStatementCreator { connection ->
            connection.prepareStatement(
                    "INSERT INTO results(team_id, task_id, start_time) VALUES (?, ?, ?)",
                    Statement.NO_GENERATED_KEYS
            ).apply {
                setLong(1, teamId)
                setLong(2, taskId)
                setLong(3, timestamp)
            }
        }

        try {
            jdbcTemplate.update(insertStatement)
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Команда №$teamId уже брала задание №$taskId" }
        }
    }

    fun finishTask(teamId: Long, taskId: Long, status: QuestStatus, answer: String? = null) {
        val timestamp = TimeUtils.currentTime(SECONDS)
        val affectedRows = jdbcTemplate.update(
                """
                    UPDATE results SET (status, finish_time, answer)=(LOWER(?)::task_status, ?, ?) 
                    WHERE team_id=? 
                    AND task_id=?
                    AND status='running'
                    AND finish_time IS NULL
                    AND answer IS NULL
                """,
                status.name, timestamp, answer, teamId, taskId
        )
        if (affectedRows != 1) {
            logger.error("finishTask fail affectedRows=[$affectedRows], teamId=[$teamId], taskId=[$taskId], answer=[$answer]")
            throw BadRequestException {
                "У команды №$teamId сейчас нет активного задания под номером [$taskId]."
            }
        }
    }

    fun getQuest(teamId: Long, taskId: Long): QuestModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                    SELECT r.*, t.* FROM results r
                    JOIN tasks t ON r.task_id=t.task_id
                    WHERE r.team_id=? AND r.task_id=?
                    """,
                    arrayOf(teamId, taskId),
                    QuestModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getQuestOrThrow(teamId: Long, taskId: Long): QuestModel {
        return getQuest(teamId, taskId) ?: throw QuestIsNotExists()
    }

    fun getCurrentQuestForTeam(teamId: Long): QuestModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                    SELECT t.*, r.* FROM results r
                        JOIN tasks t ON r.task_id=t.task_id
                    WHERE r.status='running' AND r.team_id=?;
                    """,
                    arrayOf(teamId),
                    QuestModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        } catch (e: IncorrectResultSizeDataAccessException) {
            logger.error("Team [$teamId] has more than 1 running task", e)
            null
        }
    }

    fun getCurrentQuestForUser(userId: Long): QuestModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                    SELECT t.*, r.* FROM results r
                        JOIN users u ON u.team_id=r.team_id
                        JOIN tasks t ON r.task_id=t.task_id
                    WHERE r.status='running' AND u.user_id=?
                    """,
                    arrayOf(userId),
                    QuestModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        } catch (e: IncorrectResultSizeDataAccessException) {
            logger.error("Team of user [$userId] has more than 1 running task", e)
            null
        }
    }

    fun getAllFinalQuests(): List<QuestModel> {
        return jdbcTemplate.query(
                """
                    SELECT * FROM results r
                        JOIN tasks t ON r.task_id=t.task_id
                        WHERE task_type='main'
                    ORDER BY team_id
                    """,
                QuestModel.Mapper()
        )
    }

    fun getAllPilotQuests(): List<QuestModel> {
        return jdbcTemplate.query(
                """
                    SELECT * FROM results r
                        JOIN tasks t ON r.task_id=t.task_id
                        WHERE task_type='photo' OR task_type='logic'
                    ORDER BY team_id
                    """,
                QuestModel.Mapper()
        )
    }

    fun getAllQuests(): List<QuestModel> {
        return jdbcTemplate.query(
                """
                    SELECT * FROM results r
                        JOIN tasks t ON r.task_id=t.task_id
                    ORDER BY team_id
                    """,
                QuestModel.Mapper()
        )
    }

    fun getAllRunningQuests(): List<QuestModel> {
        return jdbcTemplate.query("""
                    SELECT t.*, r.* FROM tasks t
                        JOIN results r ON t.task_id=r.task_id
                    WHERE r.status='running'
                    """,
                QuestModel.Mapper()
        )
    }

    fun getTaskStates(taskType: TaskType): List<TaskState> {
        return jdbcTemplate.query(
                """
                    SELECT t.task_id, t.task_name, t.capacity, t.task_type,
                           COUNT(r.status) FILTER (WHERE r.status='running') AS load,
                           COUNT(r.status) AS hints
                    FROM tasks t
                        LEFT JOIN results r ON t.task_id=r.task_id
                    WHERE t.task_type=LOWER(?)::task_type
                    GROUP BY t.task_id;
                    """,
                arrayOf(taskType.name),
                TaskState.Mapper()
        )
    }

    fun getTaskStates(): List<TaskState> {
        return jdbcTemplate.query(
                """
                    SELECT t.task_id, t.task_name, t.capacity, t.task_type,
                           COUNT(r.status) FILTER (WHERE r.status='running') AS load,
                           COUNT(r.status) AS hints
                    FROM tasks t
                        LEFT JOIN results r
                            ON t.task_id=r.task_id
                    GROUP BY t.task_id;
                    """,
                TaskState.Mapper()
        )
    }

    fun getAvailableTasks(teamId: Long, taskType: TaskType): List<Long> {
        return jdbcTemplate.query(
                """
                    SELECT t.task_id FROM tasks t
                        LEFT JOIN results r 
                        ON t.task_id=r.task_id AND r.team_id=? 
                    WHERE t.task_type=LOWER(?)::task_type AND r.task_id IS NULL;
                    """,
                arrayOf(teamId, taskType.name)
        ) { rs, _ -> rs.getLong(1) }
    }

    fun getAvailableTasks(teamId: Long): List<TaskModel> {
        return jdbcTemplate.query(
                """
                    SELECT t.* FROM tasks t
                        LEFT JOIN results r ON t.task_id=r.task_id AND r.team_id=? 
                    WHERE r.task_id IS NULL;
                    """,
                arrayOf(teamId),
                TaskModel.Mapper()
        )
    }

    /**
     * [RowMapperResultSetExtractor] save the result set order
     * So the last model is guaranteed to be the last task (max finish_time).
     */
    fun getCompletedTasksForTeam(teamId: Long): List<QuestModel> {
        return jdbcTemplate.query(
                """
                    SELECT t.*, r.* FROM tasks t
                    JOIN results r 
                        ON t.task_id=r.task_id 
                        AND r.team_id=?
                    """,
                arrayOf(teamId),
                QuestModel.Mapper()
        )
    }

    fun getCompletedTaskIdsForTeam(teamId: Long, taskType: TaskType): List<Long> {
        return jdbcTemplate.query(
                """
                    SELECT r.task_id FROM results r 
                        JOIN tasks t on r.task_id = t.task_id
                    WHERE r.team_id=? 
                        AND t.task_type=LOWER(?)::task_type 
                        AND r.status<>'running' 
                    """,
                arrayOf(teamId, taskType.name)
        ) { rs, _ -> rs.getLong(1) }
    }

    fun getLastQuestForUser(userId: Long): QuestModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                    SELECT t.*, r.* FROM results r
                        JOIN users u ON u.team_id=r.team_id
                        JOIN tasks t ON r.task_id=t.task_id
                    WHERE u.user_id=?
                    ORDER BY r.finish_time DESC NULLS FIRST 
                    LIMIT 1
                    """,
                    arrayOf(userId),
                    QuestModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getLastTaskForTeam(teamId: Long): TaskModel? {
        try {
            return jdbcTemplate.queryForObject(
                    """
                    SELECT t.* FROM tasks t
                        JOIN results r ON t.task_id=r.task_id
                    WHERE r.team_id=? AND r.status='running'
                    """,
                    arrayOf(teamId),
                    TaskModel.Mapper()
            )

        } catch (e: EmptyResultDataAccessException) {
            return null

        } catch (e: IncorrectResultSizeDataAccessException) {
            logger.error("Team [$teamId] has more than 1 running task", e)
            throw BadRequestException {
                "Ваша команда не может проходить задания дальше. Срочно напишите Трубникову: vk.com/trubnikovdv"
            }
        }
    }

    fun saveAttempt(userId: Long, answer: AnswerModel) {
        val timestamp = TimeUtils.currentTime(SECONDS)
        val affectedRows = jdbcTemplate.update(
                "INSERT INTO attempts(team_id, task_id, user_id, answer, time) VALUES (?, ?, ?, ?, ?)",
                answer.teamId, answer.taskId, userId, answer.answer, timestamp
        )
        if (affectedRows != 1) {
            logger.error("Fail to add answer [$answer]")
        }
    }

    fun getAttemptsCount(teamId: Long, taskId: Long): Int {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(a.answer) FROM attempts a WHERE team_id=? AND task_id=?",
                Int::class.java,
                teamId, taskId
        )
    }
}
