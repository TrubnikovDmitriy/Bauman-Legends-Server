package legends.dao

import legends.exceptions.BadRequestException
import legends.models.TaskModel
import legends.models.WitnessModel
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class WitnessDao(dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(WitnessDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun getWitnessesForTeam(teamId: Long): List<WitnessModel> {
        return jdbcTemplate.query(
                """
                    SELECT g.* FROM open_ghosts og
                    JOIN ghosts g ON g.ghost_id = og.ghost_id
                    WHERE og.team_id=?;
                    """,
                arrayOf(teamId),
                WitnessModel.Mapper()
        )
    }

    fun getWitnessByKeyword(keyword: String): WitnessModel? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM ghosts g WHERE LOWER(keyword)=LOWER(?)",
                    arrayOf(keyword),
                    WitnessModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            logger.info("Wrong keyword [$keyword]")
            throw BadRequestException { "Неверное ключ-слово." }
        }
    }

    fun inspectWitness(teamId: Long, ghostId: Long) {
        val affectedRows = try {
            jdbcTemplate.update(
                    "INSERT INTO open_ghosts(team_id, ghost_id) VALUES(?, ?)",
                    teamId, ghostId
            )
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Ваша команда уже допрашивала этого свидетеля." }
        }

        if (affectedRows != 1) {
            logger.error("Fail to open ghost ghostId=[$ghostId], teamId=[$teamId], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось допросить свидетеля." }
        }
    }

    fun getDecision(teamId: Long): Boolean? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT decision FROM witness_decision WHERE team_id=?",
                    Boolean::class.java,
                    teamId
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun setDecision(teamId: Long, decision: Boolean) {
        val affectedRows = try {
            jdbcTemplate.update(
                    "INSERT INTO witness_decision(team_id, decision) VALUES (?, ?)",
                    teamId, decision
            )
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Ваша команда уже сделала свой Выбор." }
        }

        if (affectedRows != 1) {
            logger.error("Fail to open ghost  teamId=[$teamId], decision=[$decision]")
            throw BadRequestException { "Не закрепить решение по Учёному." }
        }
    }
}