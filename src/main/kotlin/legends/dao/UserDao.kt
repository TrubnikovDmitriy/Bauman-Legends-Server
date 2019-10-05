package legends.dao

import legends.exceptions.BadRequestException
import legends.exceptions.UserNotExists
import legends.models.UserModel
import legends.models.UserRole
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class UserDao(dataSource: DataSource) {

    private val logger = LoggerFactory.getLogger(UserDao::class.java)
    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val jdbcInsert = SimpleJdbcInsert(dataSource).apply {
        withTableName("users")
        usingColumns("login", "password", "salt", "first_name", "last_name", "study_group", "vk")
        usingGeneratedKeyColumns("user_id");
    }

    fun getUserByLogin(login: String): UserModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """SELECT user_id, login, password, salt, team_id, role, first_name, last_name, study_group, vk
                            FROM users
                            WHERE LOWER(login)=LOWER(?)
                            """,
                    arrayOf(login),
                    UserModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getUserById(userId: Long): UserModel? {
        return try {
            jdbcTemplate.queryForObject(
                    """SELECT user_id, login, password, salt, team_id, role, first_name, last_name, study_group, vk
                            FROM users WHERE user_id=?""",
                    arrayOf(userId),
                    UserModel.Mapper()
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun getUserOrThrow(userId: Long): UserModel {
        return getUserById(userId) ?: throw UserNotExists(userId)
    }

    fun insertUser(user: UserModel): Long {
        val parameters = HashMap<String, Any>(8, 1f).apply {
            set("login", user.login)
            set("password", user.hashedPassword)
            set("salt", user.salt)
            set("first_name", user.firstName)
            set("last_name", user.lastName)
            set("study_group", user.group)
            set("vk", user.vkRef)
        }

        try {
            return jdbcInsert.executeAndReturnKey(parameters).toLong()
        } catch(e: DuplicateKeyException) {
            throw BadRequestException { "Такой логин [${user.login}] уже занят" }
        }
    }

    fun deleteUser(userId: Long) {
        val affectedRows = jdbcTemplate.update("DELETE FROM users WHERE user_id=?", userId)
        if (affectedRows != 1) {
            logger.error("Проблемы с удалением пользователя с userId=$userId, affectedRows=$affectedRows")
        }
        return
    }

    fun getUsersByTeamId(teamId: Long): List<UserModel> {
        return jdbcTemplate.query(
                """SELECT user_id, login, password, salt, team_id, role, first_name, last_name, study_group, vk
                            FROM users WHERE team_id=?""",
                arrayOf(teamId),
                UserModel.Mapper()
        )
    }

    fun getUsersWithoutTeam(): List<UserModel> {
        return jdbcTemplate.query(
                """SELECT user_id, login, password, salt, team_id, role, first_name, last_name, study_group, vk
                            FROM users WHERE team_id IS NULL""",
                UserModel.Mapper()
        )
    }

    fun setRole(userId: Long, role: UserRole) {
        jdbcTemplate.update("UPDATE users SET role=LOWER(?)::user_role WHERE user_id=?", role.name, userId)
    }

    fun setTeamId(userId: Long, teamId: Long?) {
        jdbcTemplate.update("UPDATE users SET team_id=? WHERE user_id=?", teamId, userId)
    }
}