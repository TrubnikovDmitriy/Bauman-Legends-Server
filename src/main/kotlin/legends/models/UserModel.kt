package legends.models

import legends.exceptions.LegendsException
import legends.exceptions.TeamIsNotPresented
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class UserModel(
        val userId: Long,
        val login: String,
        val role: UserRole,
        val teamId: Long?,
        val firstName: String,
        val lastName: String,
        val group: String,
        val vkRef: String,
        val hashedPassword: ByteArray,
        val salt: ByteArray
) {

    class Mapper : RowMapper<UserModel> {
        override fun mapRow(rs: ResultSet, rowNum: Int): UserModel? {
            return UserModel(
                    userId = rs.getLong("user_id"),
                    teamId = rs.getLong("team_id").run {
                        if (rs.wasNull()) null else this
                    },
                    login = rs.getString("login"),
                    hashedPassword = rs.getBytes("password"),
                    salt = rs.getBytes("salt"),
                    role = UserRole.valueOfSafety(rs.getString("role")),
                    firstName = rs.getString("first_name"),
                    lastName = rs.getString("last_name"),
                    group = rs.getString("study_group").toUpperCase(),
                    vkRef = rs.getString("vk")
            )
        }
    }

    fun checkCaptain(): Long {
        if (teamId == null) {
            throw TeamIsNotPresented()
        }
        if (role != UserRole.CAPTAIN) {
            throw LegendsException(HttpStatus.FORBIDDEN)
            { "Действие отклонено, так как вы не являетесь капитаном команды." }
        }
        return teamId
    }
}