package legends.exceptions

import org.springframework.http.HttpStatus

class TeamIsNotPresented : LegendsException(
        errorMessage = { "Вы не состоите в команде" },
        status = HttpStatus.NOT_FOUND
)
