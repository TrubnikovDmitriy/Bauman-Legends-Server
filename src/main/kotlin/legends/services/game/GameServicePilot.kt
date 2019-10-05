package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.exceptions.BadRequestException
import legends.exceptions.LegendsException
import legends.exceptions.QuestIsNotExists
import legends.models.*
import legends.services.TeamService.Companion.MAX_TEAM_SIZE
import legends.services.TeamService.Companion.MIN_TEAM_SIZE
import legends.utils.validateRunningStatus
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional

class GameServicePilot(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao
) : GameService {

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
        if (user.teamId != answer.teamId) {
            throw BadRequestException { "Вы не состоите в команде №${answer.teamId}" }
        }

        val quest = gameDao.getQuest(
                teamId = answer.teamId,
                taskId = answer.taskId
        ).validateRunningStatus()

        quest.answers.find {
            answer.answer.equals(it, ignoreCase = true)
        } ?: return false

        gameDao.finishTask(
                teamId = answer.teamId,
                taskId = answer.taskId,
                status = QuestStatus.SUCCESS,
                answer = answer.answer
        )
        teamDao.increaseScore(answer.teamId, quest.points)

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

    private fun selectTask(
            allTasks: List<TaskState>,
            completedTasks: List<QuestModel>
    ): Long? {

        val completedTaskIds = completedTasks.map { it.taskId }

        val lastTask = completedTasks.maxBy { it.finishTime ?: 0L }
        val taskType = when (lastTask?.taskType) {
            TaskType.LOGIC -> TaskType.PHOTO
            TaskType.PHOTO -> TaskType.LOGIC
            else -> TaskType.PHOTO
        }

        val task = allTasks
                .filter { it.taskType == taskType }
                .toMutableList()
                .apply {
                    removeIf { completedTaskIds.contains(it.taskId) }
                    sortBy { it.loadFactor }
                }
                .firstOrNull()

        return task?.taskId ?: throw LegendsException(HttpStatus.NOT_FOUND) {
            "Поздарвляем! Ваша команда прошла все квесты разогревочного этапа, ждём Вас в финале!"
        }
    }
}