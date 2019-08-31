package legends.exceptions

import org.springframework.http.HttpStatus

class UserNotExists(userId: Long) : LegendsException(
        errorMessage = { "Участника под номером [$userId] не существует." },
        status = HttpStatus.NOT_FOUND
)