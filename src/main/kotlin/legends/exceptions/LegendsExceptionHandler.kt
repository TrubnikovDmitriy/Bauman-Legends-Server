package legends.exceptions

import legends.views.ErrorView
import legends.views.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class LegendsExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(LegendsException::class)
    protected fun handleException(exception: LegendsException): ResponseEntity<ErrorView> {
        logger.warn("ExceptionHandler: ${exception.errorMessage()}")
        return exception.toResponse()
    }
}