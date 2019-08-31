package legends.exceptions

import org.springframework.http.HttpStatus

class BadRequestException(errorMessage: () -> String) : LegendsException(
        status = HttpStatus.BAD_REQUEST,
        errorMessage = errorMessage
)