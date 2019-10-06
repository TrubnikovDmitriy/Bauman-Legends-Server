package legends.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserUpdate (
        @JsonProperty(required = true, value = "vk_ref") val vkRef: String
)