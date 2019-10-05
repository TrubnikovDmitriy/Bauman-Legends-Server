package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.exceptions.BadRequestException
import legends.exceptions.QuestIsNotExists
import legends.models.*
import legends.services.TeamService.Companion.MAX_TEAM_SIZE
import legends.services.TeamService.Companion.MIN_TEAM_SIZE
import legends.utils.GameHelperUtils.convertPhotoQuestAnswer
import legends.utils.validateRunningStatus
import org.springframework.transaction.annotation.Transactional

open class GameServicePilot(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao
) : GameService {

    companion object {
        private const val FIRST_PHOTO_ID = 1L

        private val pilotTaskMap: Map<Long /* PhotoTaskId */, Long /* LogicTaskId */> = mapOf(
                1L to 11L, // Королёв
                2L to 12L, // Капсула
                3L to 13L, // Львы
                4L to 14L, // Памятники
                5L to 15L, // Собака
                6L to 16L  // Бауман RIP
        )
    }

    override fun getCurrentTask(userId: Long): TeamState {
        val quest = gameDao.getLastQuestForUser(userId)

        if (quest == null) {
            return TeamState.pause(text = "Разгоревочный этап начался! Капитан команды может взять первое задание.")
        }

        if (quest.status == QuestStatus.RUNNING) {
            return TeamState.play(quest)
        }

        val availableTaskIds = gameDao.getAvailableTasks(quest.teamId)
        if (availableTaskIds.none { it.taskType == TaskType.PHOTO || it.taskType == TaskType.LOGIC }) {
            return TeamState.stop("Поздравляем! Вы прошли все задания разогревочного этапа! Основной этап начнётся 11 октября.")
        }

        return TeamState.pause(quest = quest)
    }

    override fun startNextTask(captainId: Long): TeamState {
        val teamId = userDao.getUserOrThrow(captainId).checkCaptain()
        val membersCount = teamDao.getTeamMembersCount(teamId)

        if (membersCount !in MIN_TEAM_SIZE..MAX_TEAM_SIZE) {
            throw BadRequestException {
                "К испытаниям допускаются только те команды, в составе которых от $MIN_TEAM_SIZE до $MAX_TEAM_SIZE игроков."
            }
        }

        val runningTask = gameDao.getCurrentQuestForTeam(teamId)
        if (runningTask != null) {
            throw BadRequestException {
                "Вы не можете приступить к следующему заданию, пока не завершите предыдущее №${runningTask.taskId}."
            }
        }

        val allTasks = gameDao.getTaskStates()
        val completedTasks = gameDao.getCompletedTasksForTeam(teamId)

        val nextTaskId = selectTask(allTasks, completedTasks) ?: return TeamState.stop(
                "Поздравляем! Вы прошли все задания разогревочного этапа! " +
                        "Основной этап начнётся 11 октября на территории Главного Здания МГТУ.")

        gameDao.startTask(teamId, nextTaskId)

        val quest = gameDao.getQuestOrThrow(teamId, nextTaskId)
        return TeamState.play(quest)
    }

    @Transactional
    override fun tryAnswer(userId: Long, dto: AnswerDto): Boolean {
        val answer = dto.convert()
        val user = userDao.getUserOrThrow(userId)

        user.checkTeam(answer.teamId)

        val quest = gameDao.getQuest(
                teamId = answer.teamId,
                taskId = answer.taskId
        ).validateRunningStatus()

        convertAnswers(quest).find {
            answer.answer.equals(it, ignoreCase = true)
        } ?: return false

        gameDao.finishTask(
                teamId = answer.teamId,
                taskId = answer.taskId,
                status = QuestStatus.SUCCESS,
                answer = answer.answer
        )
        teamDao.increaseMoney(answer.teamId, quest.points)

        return true
    }

    override fun skipTask(userId: Long) {
        val teamId = userDao.getUserOrThrow(userId).checkCaptain()
        val task = gameDao.getCurrentQuestForTeam(teamId) ?: throw QuestIsNotExists()

        if (!task.skipPossible) {
            throw BadRequestException { "Текущее задания нельзя пропустить." }
        }

        gameDao.finishTask(
                teamId = teamId,
                taskId = task.taskId,
                status = QuestStatus.SKIP
        )
    }

    private fun convertAnswers(questModel: QuestModel): List<String> {
        if (questModel.taskType != TaskType.PHOTO) {
            return questModel.answers
        }

        val photoAnswer = convertPhotoQuestAnswer(
                answers = questModel.answers,
                teamId = questModel.teamId
        )
        return listOf(photoAnswer)
    }

    private fun selectTask(
            allTasks: List<TaskState>,
            completedTasks: List<QuestModel>
    ): Long? {

        if (completedTasks.isEmpty()) {
            // Первый фотоквест фиксирован и должен начинаться возле УЛК.
            return FIRST_PHOTO_ID
        }

        val lastTask = completedTasks.maxBy { it.finishTime ?: 0L }
        if (lastTask?.taskType == TaskType.PHOTO) {
            // После каждого фотоквеста идёт задание на логику
            // и они жестко связаны друг с другом.
            return pilotTaskMap[lastTask.taskId]
        }

        // Все последующие фотоквесты берутся в рандомном порядке
        val completedTaskIds = completedTasks.map { it.taskId }
        val task = allTasks
                .filter { it.taskType == TaskType.PHOTO }
                .toMutableList()
                .apply {
                    removeIf { completedTaskIds.contains(it.taskId) }
                    sortBy { it.loadFactor }
                }
                .firstOrNull()

        return task?.taskId
    }
}