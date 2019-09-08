package legends.utils

import legends.dto.TaskDto
import legends.dto.UserSignUp
import legends.models.TaskType.LOGIC
import legends.models.TaskType.PHOTO

object ValidationUtils {

    const val INVALID_ID = -1L

    private const val MIN_PASSWORD_LENGTH = 3
    private val NAMING_LENGTH_RANGE = 2..25

    /**
     * @return `null` in case of success validation
     */
    fun validateAndGetReason(userSignUp: UserSignUp): String? {
        userSignUp.run {
            if (firstName.length !in NAMING_LENGTH_RANGE) {
                return "Имя неверной длины."
            }
            if (lastName.length !in NAMING_LENGTH_RANGE) {
                return "Фамилия неверной длины."
            }
            if (login.length !in NAMING_LENGTH_RANGE) {
                return "Логин неверной длины."
            }
            if (password.length < MIN_PASSWORD_LENGTH) {
                return "Пароль не может быть меньше $MIN_PASSWORD_LENGTH символов."
            }
            if (!group.contains(Regex("\\d"))) {
                return "Неверный формат группы."
            }
            if (vkRef.isBlank()) {
                return "Ссылка на ВК отстуствует."
            }
            return null
        }
    }

    /**
     * @return `null` in case of success validation
     */
    fun validateAndGetReason(dto: TaskDto): String? {
        dto.run {
            if (taskName.trim().length !in NAMING_LENGTH_RANGE) {
                return "Название задания неверной длины."
            }
            if (html.isBlank()) {
                return "Отсутствует само задание."
            }
            if (duration != null && duration <= 0) {
                return "Длителньость прохождения этапа не может быть нулевой или отрицательной."
            }
            if (duration != null && (taskType == PHOTO || taskType == LOGIC)) {
                return "Задания разогревочного этапа не могут быть ограничены по времени."
            }
            if (answers.isEmpty()) {
                return "Отсутствуют возможные варианты ответов на задание."
            }
            for (answer in answers) {
                if (answer.isBlank()) return "Один из ответов путой."
            }
            if (capacity <= 0) {
                return "Вместимость задания не может быть нулевой и меньше."
            }
            return null
        }
    }
}