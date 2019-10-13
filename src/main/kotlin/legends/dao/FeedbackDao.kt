package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.NotFoundException
import legends.models.FeedbackModel
import legends.models.HintModel
import legends.models.OpenHintModel
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class FeedbackDao(private val dataSource: DataSource) {

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource).apply {
        withTableName("feedback")
        usingColumns(
                "user_id",
                "pilot_mark",
                "final_mark",
                "legends_mark",
                "site_mark",
                "task_mark",
                "ghost_mark",
                "best_task",
                "worst_task",
                "known_from",
                "message"
        )
    }

    fun saveFeedback(feedback: FeedbackModel) {
        val parameters = HashMap<String, Any>(12, 1f).apply {
            set("user_id", feedback.userId)
            set("pilot_mark", feedback.pilotMark)
            set("final_mark", feedback.finalMark)
            set("legends_mark", feedback.legendsMark)
            set("site_mark", feedback.siteMark)
            set("task_mark", feedback.taskMark)
            set("ghost_mark", feedback.ghostMark)
            set("best_task", feedback.bestTask)
            set("worst_task", feedback.worstTask)
            set("known_from", feedback.from)
            set("message", feedback.message)
        }

        jdbcInsert.execute(parameters)
    }

    fun isUserFeedbackExists(userId: Long): Boolean {
        return jdbcTemplate.query("SELECT legends_mark FROM feedback WHERE user_id=?", arrayOf(userId)) {
            rs, _ -> rs.getInt("legends_mark")
        }.isNotEmpty()
    }
}
