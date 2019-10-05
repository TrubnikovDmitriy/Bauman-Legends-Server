package legends.services

import legends.dao.GameDao
import legends.dao.TaskDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.logic.GameState
import legends.models.GameStatus.*
import legends.models.QuestModel
import legends.models.TaskState
import legends.models.TaskType
import legends.models.TeamModel
import org.springframework.stereotype.Service

@Service
class ModeratorService(
        private val gameDao: GameDao,
        private val teamDao: TeamDao,
        private val taskDao: TaskDao,
        private val userDao: UserDao
) {

    fun getAllQuests(userId: Long, withCompleted: Boolean): Map<Long, List<QuestModel>> {
        userDao.getUserOrThrow(userId).checkModerator()

        val questList = when(GameState.status) {
            REGISTRATION -> emptyList()
            PILOT -> gameDao.getAllPilotQuests()
            FINAL -> gameDao.getAllFinalQuests()
            FINISH -> gameDao.getAllQuests()
        }

        val maxTaskCount = GameState.getMaxTaskCount()
        return questList
                .groupBy { quest -> quest.teamId }
                .filterValues { withCompleted || it.size != maxTaskCount }
    }

    fun getAllTeams(userId: Long): List<TeamModel> {
        userDao.getUserOrThrow(userId).checkModerator()
        return teamDao.getAllTeams()
    }

    fun getTaskStates(userId: Long, taskType: TaskType): List<TaskState> {
        userDao.getUserOrThrow(userId).checkModerator()
        return gameDao.getTaskStates(taskType)
    }
}