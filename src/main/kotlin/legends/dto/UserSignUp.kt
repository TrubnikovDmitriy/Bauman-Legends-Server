package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserSignUp (
        @JsonProperty(required = true, value = "first_name") val firstName: String,
        @JsonProperty(required = true, value = "last_name") val lastName: String,
        @JsonProperty(required = true, value = "login") val login: String,
        @JsonProperty(required = true, value = "password") val password: String,
        @JsonProperty(required = true, value = "group") val group: String,
        @JsonProperty(required = true, value = "vk_ref") val vkRef: String
) {
    companion object {
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 20
    }

    fun isValid(): Boolean {
        if (firstName.length !in MIN_LENGTH..MAX_LENGTH) return false
        if (lastName.length !in MIN_LENGTH..MAX_LENGTH) return false
        if (login.length !in MIN_LENGTH..MAX_LENGTH) return false
        if (password.length < MIN_LENGTH) return false
        if (!group.contains(Regex("\\d"))) return false
        if (vkRef.isBlank()) return false
        return true
    }
}