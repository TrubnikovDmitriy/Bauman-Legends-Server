package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.UserModel
import legends.models.UserRole

data class UserSignUp (
        @JsonProperty(required = true, value = "first_name") val firstName: String,
        @JsonProperty(required = true, value = "last_name") val lastName: String,
        @JsonProperty(required = true, value = "login") val login: String,
        @JsonProperty(required = true, value = "password") val password: String,
        @JsonProperty(required = true, value = "group") val group: String,
        @JsonProperty(required = true, value = "vk_ref") val vkRef: String
) {
    fun convert(userId: Long, hash: ByteArray, salt: ByteArray): UserModel {
        return UserModel(
                userId = userId,
                login = login.trim(),
                role = UserRole.PLAYER,
                teamId = null,
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                group = group.toUpperCase(),
                vkRef = vkRef.trim(),
                hashedPassword = hash,
                salt = salt
        )
    }
}
