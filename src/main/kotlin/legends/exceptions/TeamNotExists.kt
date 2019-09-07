package legends.exceptions

import org.springframework.http.HttpStatus

class TeamNotExists(teamId: Long) : LegendsException(
        errorMessage = { "Команды под номером [$teamId] не существует." },
        status = HttpStatus.NOT_FOUND
)