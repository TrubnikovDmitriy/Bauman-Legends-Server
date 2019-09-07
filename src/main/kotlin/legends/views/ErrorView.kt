package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.exceptions.LegendsException
import org.springframework.http.ResponseEntity

class ErrorView(@JsonProperty(value = "message") val message: String)

fun LegendsException.toResponse(): ResponseEntity<ErrorView> {
    val errorView = ErrorView(errorMessage())
    return ResponseEntity(errorView, status)
}