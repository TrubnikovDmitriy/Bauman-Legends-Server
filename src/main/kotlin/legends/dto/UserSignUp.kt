package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserSignUp (
        @JsonProperty(required = true, value = "first_name") val firstName: String,
        @JsonProperty(required = true, value = "last_name") val lastName: String,
        @JsonProperty(required = true, value = "login") val login: String,
        @JsonProperty(required = true, value = "password") val password: String,
        @JsonProperty(required = true, value = "group") val group: String,
        @JsonProperty(required = true, value = "vk_ref") val vkRef: String
)
