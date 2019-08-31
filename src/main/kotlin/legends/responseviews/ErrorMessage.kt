package legends.responseviews

import com.fasterxml.jackson.annotation.JsonProperty

class ErrorMessage(@JsonProperty(value = "message") val message: String)
