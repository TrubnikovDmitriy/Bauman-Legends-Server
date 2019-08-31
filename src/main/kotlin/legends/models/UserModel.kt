package legends.models

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
                    teamId = rs.getLong("team_id"),
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
}