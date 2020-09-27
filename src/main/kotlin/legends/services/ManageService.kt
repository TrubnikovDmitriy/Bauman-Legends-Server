package legends.services

import legends.dao.GameDao
import legends.dao.TaskDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.GameStageUpdate
import legends.exceptions.BadRequestException
import legends.exceptions.NotFoundException
import legends.logic.GameState
import legends.logic.QuestTimer
import legends.models.*
import legends.models.GameStage.*
import legends.utils.GameHelperUtils.convertPhotoQuestAnswer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ManageService(
        private val gameDao: GameDao,
        private val teamDao: TeamDao,
        private val taskDao: TaskDao,
        private val userDao: UserDao,
        private val questTimer: QuestTimer
) {
    private val logger = LoggerFactory.getLogger(ManageService::class.java)

    fun getAllQuests(userId: Long, withCompleted: Boolean): Map<Long, List<QuestModel>> {
        userDao.getUserOrThrow(userId).checkModerator()

        val questList = when(GameState.stage) {
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
        userDao.getUserOrThrow(userId).checkRevisor()
        return teamDao.getAllTeams()
    }

    fun getTaskStates(userId: Long, taskType: TaskType): List<TaskState> {
        userDao.getUserOrThrow(userId).checkModerator()
        return gameDao.getTaskStates(taskType)
    }

    fun getTaskForTeam(userId: Long, teamId: Long): TaskModel {
        userDao.getUserOrThrow(userId).checkRevisor()

        val lastTask = gameDao.getLastTaskForTeam(teamId)

        if (lastTask == null) {
            teamDao.getTeamById(teamId) ?: throw NotFoundException { "Команды №$teamId не существует." }
            throw NotFoundException { "Команда №$teamId еще не приступила к выполнению заданий." }
        }

        return when(lastTask.taskType) {
            TaskType.PHOTO -> {
                val photoAnswer = convertPhotoQuestAnswer(
                        answers = lastTask.answers,
                        teamId = teamId
                )
                lastTask.copy(answers = lastTask.answers + photoAnswer)
            }
            else -> lastTask
        }
    }

    fun updateGameStage(adminId: Long, stageUpdate: GameStageUpdate) {
        userDao.getUserOrThrow(adminId).checkRevisor()
        val secretWord = System.getenv("LB_ADMIN_SECRET") ?: "lb2020"
        if (secretWord != stageUpdate.secret) {
            throw BadRequestException { "Неверное кодовое слово." }
        }
        val oldStage = GameState.stage
        GameState.updateStage(stageUpdate.stage)

        updateStage(from = oldStage, to = stageUpdate.stage)
    }

    private fun updateStage(from: GameStage, to: GameStage) {
        if (from == to) return

        if (to == FINAL) {
            questTimer.rescheduleAllQuests()
        }
    }
}
