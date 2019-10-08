package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.NotFoundException
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
class HintDao(private val dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(HintDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource).apply {
        withTableName("hints")
        usingColumns("task_id", "html", "cost")
        usingGeneratedKeyColumns("hint_id")
    }

    fun createHint(hint: HintModel): Long {
        val parameters = HashMap<String, Any>(4, 1f).apply {
            set("task_id", hint.taskId)
            set("cost", hint.cost)
            set("html", hint.html)
        }

        return jdbcInsert.executeAndReturnKey(parameters).toLong()
    }

    fun updateHint(hint: HintModel) {
        val affectedRows = jdbcTemplate.update(
                "UPDATE hints SET (task_id, cost, html) = (?, ?, ?) WHERE hint_id=?",
                hint.taskId, hint.cost, hint.html, hint.hintId
        )
        if (affectedRows != 1) {
            logger.error("Fail to update hint with id=[${hint.hintId}], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось обновить подсказку (изменено подсказок $affectedRows)." }
        }
    }

    fun getHintOrThrow(hintId: Long): HintModel {
        return getHintById(hintId) ?: throw NotFoundException {
            "Не удалось найти подсказку под номером [$hintId]."
        }
    }

    fun getHintById(hintId: Long): HintModel? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM hints WHERE hint_id=?",
                    arrayOf(hintId),
                    HintModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            logger.error("Fail to get hint with id [$hintId]", e)
            null
        }
    }

    fun getHintsForTeam(teamId: Long): List<OpenHintModel> {
        return jdbcTemplate.query(
                """
                    SELECT h.*, oh.team_id FROM results r
                        JOIN hints h on r.task_id=h.task_id
                        LEFT JOIN open_hints oh ON h.hint_id=oh.hint_id
                    WHERE r.status='running' AND r.team_id=?;
                    """,
                arrayOf(teamId),
                OpenHintModel.Mapper()
        )
    }

    fun openHintForTeam(hintId: Long, teamId: Long) {

        val affectedRows = try {
            jdbcTemplate.update(
                    "INSERT INTO open_hints(hint_id, team_id) VALUES (?, ?)",
                    hintId, teamId
            )
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Ваша команда уже приобрела эту подсказку." }
        }

        if (affectedRows != 1) {
            logger.error("Fail to open hint hintId=[$hintId], teamId=[$teamId], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось купить подсказку." }
        }
    }

    fun getAllHints(): List<HintModel> {
        return jdbcTemplate.query("SELECT * FROM hints", HintModel.Mapper())
    }

    fun deleteHint(hintId: Long) {
        val affectedRows = jdbcTemplate.update("DELETE FROM hints WHERE hint_id=?", hintId)
        if (affectedRows != 1) {
            logger.error("Fail to delete hint with id=[$hintId], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось удалить подсказку (изменено подсказок $affectedRows)." }
        }
    }
}
