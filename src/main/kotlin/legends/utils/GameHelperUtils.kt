package legends.utils

import legends.exceptions.BadRequestException
import legends.models.QuestModel
import legends.models.QuestStatus.*

fun QuestModel?.validateRunningStatus(): QuestModel {
    val action = when (this?.status) {
        RUNNING -> return this
        SUCCESS -> "уже ответила на это задание"
        SKIP -> "пропустила это задание"
        FAIL -> "не успела ответить на это задание"
        else -> "в данный момент не выполняет это задание"
    }
    throw BadRequestException { "Ваша команда $action. Пожалуйста, обновите страницу." }
}