package legends.services

import legends.dao.GameDao
import legends.dao.UserDao
import legends.exceptions.BadRequestException
import legends.logic.GameState
import legends.models.BaumanModel
import legends.models.TaskType
import org.springframework.stereotype.Service

@Service
class BaumanService(
        private val gameDao: GameDao,
        private val userDao: UserDao
) {

    fun getBaumanFragments(userId: Long): BaumanModel {
        val teamId = userDao.getUserOrThrow(userId).teamId
        if (teamId == null) {
            throw BadRequestException { "Вы не состоите в команде." }
        }
        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(teamId, TaskType.MAIN)

        val size = GameState.MAX_FINAL_TASK_COUNT
        val baumanFragments = Array<Int?>(size) { null }

        completedTaskIds.forEach { taskId ->
            var fragmentId: Int = (teamId * taskId % size).toInt()
            while(baumanFragments[fragmentId] == null) {
                fragmentId = (fragmentId + 1) % size
            }
            baumanFragments[fragmentId] = fragmentId
        }

        return BaumanModel(fragments = baumanFragments.filterNotNull().sorted())
    }
}