package legends.services

import legends.dao.GameDao
import legends.dao.UserDao
import legends.exceptions.BadRequestException
import legends.models.BaumanModel
import legends.models.TaskType
import org.springframework.stereotype.Service

@Service
class BaumanService(
        private val gameDao: GameDao,
        private val userDao: UserDao
) {
    companion object {
        private const val BASE_URL = "https://testing.legends.bmstu.ru:451/documents/"
        private const val NOT_FOUND_URL = BASE_URL + "not_found"
        private val urlsMap: Map<Int, String> = mapOf(
                0 to BASE_URL + "0-s0esj0g3r.pdf",
                1 to BASE_URL + "1-f27389f3f.pdf",
                2 to BASE_URL + "2-1sk8234f0.pdf",
                3 to BASE_URL + "3-13f249h34.pdf",
                4 to BASE_URL + "4-f123941f5.pdf",
                5 to BASE_URL + "5-04efty9we.pdf",
                6 to BASE_URL + "6-3h5t48u0r.jpg",
                7 to BASE_URL + "7-m2v45ciq3.pdf",
                8 to BASE_URL + "8-w33d4mrd8.jpg",
                9 to BASE_URL + "9-acEOw4v90.pdf"
        )
    }

    fun getBaumanFragments(userId: Long): List<BaumanModel> {
        val teamId = userDao.getUserOrThrow(userId).teamId
        if (teamId == null) {
            throw BadRequestException { "Вы не состоите в команде." }
        }

        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(teamId, TaskType.MAIN)
        val completedTaskCount = completedTaskIds.size

        val urls = Array(completedTaskCount) { documentId ->
            BaumanModel(documentId, urlsMap[documentId] ?: NOT_FOUND_URL)
        }.toList()

        return urls
    }
}