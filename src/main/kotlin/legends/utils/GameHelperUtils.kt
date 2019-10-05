package legends.utils

import legends.exceptions.BadRequestException
import legends.models.QuestModel
import legends.models.QuestStatus.*

object GameHelperUtils {

    private val secureUtils = SecureUtils()

    /**
     * У фотоквеста будет только один ответ. Однако, несмотря на это, для каждой команды
     * он должен быть индвидуальным, чтобы избежать читерства. Поэтому ответ на фотоквест
     * формируется как хеш от настоящего ответа и номера команды.
     */
    fun convertPhotoQuestAnswer(answers: List<String>, teamId: Long): String {
        val answer = answers.min() ?: throw IllegalStateException("Answers size is null")
        val byteAnswer = secureUtils.getHash(answer, teamId.toString().toByteArray())
        val uuidAnswer = secureUtils.uuidFromBytes(byteAnswer)
        return uuidAnswer.substring(9..22)
    }
}


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
