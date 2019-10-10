package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.LegendsException
import legends.exceptions.TeamNotExists
import legends.models.TeamModel
import legends.utils.SecureUtils
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class TeamDao(dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(TeamDao::class.java)
    private val secureUtils = SecureUtils()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource).apply {
        withTableName("teams")
        usingColumns("leader_id", "team_name", "invite_code")
        usingGeneratedKeyColumns("team_id")
    }

    fun createTeam(leaderId: Long, teamName: String): TeamModel {

        val inviteCode = secureUtils.generateRandomString()
        val parameters = HashMap<String, Any>(4, 1f).apply {
            set("leader_id", leaderId)
            set("team_name", teamName)
            set("invite_code", inviteCode)
        }

        try {
            val teamId = jdbcInsert.executeAndReturnKey(parameters).toLong()
            return TeamModel(
                    teamId = teamId,
                    teamName = teamName,
                    leaderId = leaderId,
                    score = 0,
                    money = 0,
                    inviteCode = inviteCode,
                    size = 1
            )
        } catch(e: DuplicateKeyException) {
            throw  LegendsException(HttpStatus.BAD_REQUEST)
            { "Команда с названием \"$teamName\" уже существует." }
        }
    }

    fun updateTeamName(teamId: Long, teamName: String) {
        try {
            jdbcTemplate.update("UPDATE teams SET team_name=? WHERE team_id=?", teamName, teamId)
        } catch(e: DuplicateKeyException) {
            throw  LegendsException(HttpStatus.BAD_REQUEST)
            { "Команда с названием \"$teamName\" уже существует." }
        }
    }

    fun getTeamOrThrow(teamId: Long): TeamModel {
        return getTeamById(teamId) ?: throw TeamNotExists(teamId)
    }

    fun getTeamById(teamId: Long): TeamModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                        SELECT t.*, COUNT(u.user_id) AS size
                        FROM teams t
                            LEFT JOIN users u ON t.team_id=u.team_id
                        WHERE t.team_id=?
                        GROUP BY t.team_id;""",
                    arrayOf(teamId),
                    TeamModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getTeamByUser(userId: Long): TeamModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """
                        SELECT t.*, COUNT(u.user_id) AS size
                        FROM users me
                            JOIN teams t ON me.team_id = t.team_id
                            LEFT JOIN users u ON t.team_id=u.team_id
                        WHERE me.user_id=?
                        GROUP BY t.team_id;
                        """,
                    arrayOf(userId),
                    TeamModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun increaseScore(teamId: Long, score: Int) {
        jdbcTemplate.update("UPDATE teams SET score=score+? WHERE team_id=?", score, teamId)
    }

    fun increaseMoney(teamId: Long, score: Int) {
        jdbcTemplate.update("UPDATE teams SET money=money+? WHERE team_id=?", score, teamId)
    }

    fun decreaseMoney(teamId: Long, cost: Int) {
        val affectedRows = jdbcTemplate.update(
                """
                    UPDATE teams SET money=money-?
                    WHERE team_id=? AND money>=?
                    """,
                cost, teamId, cost
        )
        if (affectedRows == 0) {
            logger.info("Not enough money for teamId=[$teamId], cost=[$cost]")
            throw BadRequestException { "Не хватает экстра-баллов для совершения операции." }
        }
        if (affectedRows != 1) {
            logger.error("Fail to decrease money teamId=[$teamId], cost=[$cost], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось совершить операцию" }
        }
    }

    fun decreaseScore(teamId: Long, cost: Int) {
        val affectedRows = jdbcTemplate.update(
                """
                    UPDATE teams SET score=score-?
                    WHERE team_id=? AND score>=?
                    """,
                cost, teamId, cost
        )
        if (affectedRows == 0) {
            logger.info("Not enough score for teamId=[$teamId], cost=[$cost]")
            throw BadRequestException { "Не хватает баллов для совершения операции." }
        }
        if (affectedRows != 1) {
            logger.error("Fail to decrease score teamId=[$teamId], cost=[$cost], affectedRows=[$affectedRows]")
            throw BadRequestException { "Не удалось совершить операцию" }
        }
    }

    fun getAllTeams(): List<TeamModel> {
        return jdbcTemplate.query(
                """
                    SELECT t.*, COUNT(u.user_id) AS size
                    FROM teams t
                    LEFT JOIN users u ON t.team_id=u.team_id
                    GROUP BY t.team_id;
                    """,
                TeamModel.Mapper()
        )
    }

    fun setTeamLeader(captainId: Long, teamId: Long) {
        jdbcTemplate.update("UPDATE teams SET leader_id=? WHERE team_id=?", captainId, teamId)
    }

    fun deleteTeam(teamId: Long) {
        val affectedRow = jdbcTemplate.update("DELETE FROM teams WHERE team_id=?", teamId)
        if (affectedRow != 1) {
            logger.error("Fail to delete team with team_id=[$teamId]")
        }
    }

    fun getTeamMembersCount(teamId: Long): Int {
        return jdbcTemplate.queryForObject<Int>(
                "SELECT COUNT(user_id) FROM users WHERE team_id=?",
                Int::class.java,
                teamId
        )
    }
}