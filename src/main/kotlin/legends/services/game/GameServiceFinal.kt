package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.exceptions.BadRequestException
import legends.logic.GameState
import legends.logic.QuestTimer
import legends.models.*
import legends.services.TeamService.Companion.MAX_TEAM_SIZE
import legends.services.TeamService.Companion.MIN_TEAM_SIZE
import legends.utils.validateRunningStatus
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

open class GameServiceFinal(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao,
        private val questTimer: QuestTimer
) : GameService {

    private val logger = LoggerFactory.getLogger(GameServiceFinal::class.java)

    override fun getCurrentTask(userId: Long): TeamState {
        val quest = gameDao.getLastQuestForUser(userId)?.takeIf { it.taskType == TaskType.MAIN }

        if (quest == null) {
            return TeamState.pause(text = "Основной этап начался! Капитан команды может взять первое задание.")
        }

        val attemptCount = gameDao.getAttemptsCount(quest.teamId, quest.taskId)

        if (quest.status == QuestStatus.RUNNING) {
            return TeamState.play(quest, attemptCount)
        }

        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(quest.teamId, TaskType.MAIN)
        if (completedTaskIds.size == GameState.getMaxTaskCount()) {
            return TeamState.stop("Поздравляем! Вы прошли почти все задания, осталось лишь одно. " +
                    "Ждём Вас тут 55°45'52.1\"N 37°41'18.4\"E")
        }

        return TeamState.pause(quest = quest, attempts = attemptCount)
    }

    override fun startNextTask(captainId: Long): TeamState {
        val teamId = userDao.getUserOrThrow(captainId).checkCaptain()
        val membersCount = teamDao.getTeamMembersCount(teamId)

        if (membersCount !in MIN_TEAM_SIZE..MAX_TEAM_SIZE) {
            throw BadRequestException {
                "К испытаниям допускаются только те команды, в составе которых от $MIN_TEAM_SIZE до $MAX_TEAM_SIZE игроков."
            }
        }

        val taskStates = gameDao.getTaskStates(TaskType.MAIN)
        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(teamId, TaskType.MAIN)

        val nextTaskId = selectTask(taskStates, completedTaskIds) ?: return TeamState
                .stop("Поздравляем! Вы прошли все испытания, Легенды Бауманки 2020 завершены!")

        gameDao.startTask(teamId, nextTaskId)
        val quest = gameDao.getQuestOrThrow(teamId, nextTaskId)
        questTimer.startTimer(quest)

        return TeamState.play(quest, attempts = 0)
    }

    override fun tryAnswer(userId: Long, dto: AnswerDto): Boolean {
        val answer = dto.convert()
        if (answer.answer.isBlank()) return false
        val user = userDao.getUserOrThrow(userId)

        user.checkTeam(answer.teamId)

        val quest = gameDao.getQuest(
                teamId = answer.teamId,
                taskId = answer.taskId
        ).validateRunningStatus()

        checkAttempts(quest, answer)
        gameDao.saveAttempt(user.userId, answer)

        quest.answers.find {
            answer.answer.equals(it, ignoreCase = true)
        } ?: run {
            checkAttempts(quest, answer) // check if attempts is exceeded
            return false
        }

        finishTask(quest, answer, QuestStatus.SUCCESS)
        return true
    }

    override fun skipTask(userId: Long) {
        throw BadRequestException { "Нельзя пропустить задание основного этапа." }
    }

    @Synchronized
    @Transactional
    open fun finishTask(quest: QuestModel, answer: AnswerModel, status: QuestStatus) {
        questTimer.cancelTimer(quest)
        gameDao.finishTask(
                teamId = answer.teamId,
                taskId = answer.taskId,
                status = status,
                answer = answer.answer
        )
        teamDao.increaseScore(answer.teamId, quest.points)
    }

    private fun checkAttempts(quest: QuestModel, answer: AnswerModel) {
        if (quest.maxAttempts == null) {
            return
        }
        val attempts = gameDao.getAttemptsCount(
                teamId = answer.teamId,
                taskId = answer.taskId
        )
        if (attempts >= quest.maxAttempts) {
            finishTask(quest, answer, QuestStatus.FAIL)
            throw BadRequestException {
                "Количество попыток ответа на задание превышено [$attempts/${quest.maxAttempts}]."
            }
        }
    }

    private fun selectTask(
            allTasks: List<TaskState>,
            completedTaskIds: List<Long>
    ): Long? {

        if (completedTaskIds.size == GameState.getMaxTaskCount()) {
            return null
        }

        val sortedTaskList = allTasks
                .filter { it.taskType == TaskType.MAIN  }
                .toMutableList()
                .apply {
                    removeIf { completedTaskIds.contains(it.taskId) }
                    sortBy { it.hints }
                    sortBy { it.loadFactor }
                }

        val nextTaskId = sortedTaskList.firstOrNull { it.loadFactor < 1f }?.taskId

        if (nextTaskId == null && sortedTaskList.isNotEmpty()) {
            logger.error("Tasks overload [$sortedTaskList]")
            throw BadRequestException {
                "К сожалению, все точки сейчас заняты. Пожалуйста, попробуйте еще раз через 5 минут."
            }
        }

        if (nextTaskId == null) {
            logger.error("There is no available tasks: completedTaskIds=[$completedTaskIds], allTasks=[$allTasks]")
            throw BadRequestException {
                "Не удалось взять следующее задание. Попробуйте позже."
            }
        }

        return nextTaskId
    }

    override fun getFact(userId: Long): FactModel? {
        val quest = gameDao.getLastQuestForUser(userId)?.takeIf { it.taskType == TaskType.MAIN }
        if (quest?.status != QuestStatus.SUCCESS) {
            return null
        }
        return gameDao.getFact(quest.taskId)
    }
}