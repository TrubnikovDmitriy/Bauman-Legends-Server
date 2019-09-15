package legends.exceptions

import org.springframework.http.HttpStatus

class QuestIsNotExists : LegendsException(
        errorMessage = { "Ваша команда сейчас не выполняет никаких заданий." },
        status = HttpStatus.BAD_REQUEST
)