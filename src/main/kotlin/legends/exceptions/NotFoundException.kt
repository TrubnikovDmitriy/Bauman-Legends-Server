package legends.exceptions

import org.springframework.http.HttpStatus

class NotFoundException(errorMessage: () -> String) : LegendsException(
        status = HttpStatus.NOT_FOUND,
        errorMessage = errorMessage
)