package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.UserModel
import legends.models.UserRole

data class UserView(
        @JsonProperty("user_id") val userId: Long,
        @JsonProperty("login") val login: String,
        @JsonProperty("role") val role: UserRole,
        @JsonProperty("team_id") val teamId: Long?,
        @JsonProperty("first_name") val firstName: String,
        @JsonProperty("last_name") val lastName: String,
        @JsonProperty("group") val group: String,
        @JsonProperty("vk_ref") val vkRef: String
) {
    constructor(user: UserModel) : this(
            userId = user.userId,
            teamId = user.teamId,
            login = user.login,
            role = user.role,
            firstName = user.firstName,
            lastName = user.lastName,
            group = user.group.toUpperCase(),
            vkRef = user.vkRef
    )
}

fun List<UserModel>.toView(): List<UserView>  = map { UserView(it) }
