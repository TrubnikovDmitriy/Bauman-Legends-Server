package legends.dao

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
                    inviteCode = inviteCode,
                    size = 1
            )
        } catch(e: DuplicateKeyException) {
            throw  LegendsException(HttpStatus.BAD_REQUEST)
            { "Команда с таким названием [$teamName] уже существует." }
        }
    }

    fun getTeamOrThrow(teamId: Long): TeamModel {
        return getTeamById(teamId) ?: throw TeamNotExists(teamId)
    }

    fun getTeamById(teamId: Long): TeamModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """SELECT t.team_id, t.team_name, t.leader_id, t.score, t.invite_code, COUNT(u.user_id) AS size 
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

    fun getAllTeams(): List<TeamModel> {
        return jdbcTemplate.query(
                """SELECT t.team_id, t.team_name, t.leader_id, t.score, t.invite_code, COUNT(u.user_id) AS size 
                        FROM teams t 
                        LEFT JOIN users u ON t.team_id=u.team_id 
                        GROUP BY t.team_id;""",
                TeamModel.Mapper()
        )
    }
}