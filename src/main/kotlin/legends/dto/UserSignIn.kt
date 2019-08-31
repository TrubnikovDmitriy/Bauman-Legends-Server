package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserSignIn (
        @JsonProperty(required = true, value = "login") val login: String,
        @JsonProperty(required = true, value = "password") val password: String
)