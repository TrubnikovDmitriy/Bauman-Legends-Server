package legends.exceptions

import org.springframework.http.HttpStatus

class TaskIsNotExists(taskId: Long) : LegendsException(
        errorMessage = { "Задания под номером [${taskId}] не существует." },
        status = HttpStatus.NOT_FOUND
)