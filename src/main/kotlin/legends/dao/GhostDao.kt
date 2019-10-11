package legends.dao

import legends.exceptions.BadRequestException
import legends.models.GhostModel
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class GhostDao(dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(GhostDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)

    fun getGhostsForTeam(teamId: Long): List<GhostModel> {
        return jdbcTemplate.query(
                """
                    SELECT g.* FROM open_ghosts og
                    JOIN ghosts g ON g.ghost_id = og.ghost_id
                    WHERE og.team_id=?;
                    """,
                arrayOf(teamId),
                GhostModel.Mapper()
        )
    }

    fun getGhostByKeyword(keyword: String): GhostModel? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM ghosts g WHERE LOWER(keyword)=LOWER(?)",
                    arrayOf(keyword),
                    GhostModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            logger.info("Wrong keyword [$keyword]")
            throw BadRequestException { "Неверное ключ-слово." }
        }
    }

    fun openGhost(teamId: Long, ghostId: Long) {
        val affectedRows = try {
            jdbcTemplate.update(
                    "INSERT INTO open_ghosts(team_id, ghost_id) VALUES(?, ?)",
                    teamId, ghostId
            )
        } catch (e: DuplicateKeyException) {
            throw BadRequestException { "Ваша команда уже находила этого призрака." }
        }

        if (affectedRows != 1) {
            logger.error("Fail to open ghost ghostId=[$ghostId], teamId=[$teamId], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось открыть призрака." }
        }
    }
}