package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.exceptions.BadRequestException
import legends.logic.QuestTimer
import legends.models.QuestStatus
import legends.models.TaskState
import legends.models.TaskType
import legends.models.TeamState
import legends.services.TeamService.Companion.MAX_TEAM_SIZE
import legends.services.TeamService.Companion.MIN_TEAM_SIZE
import legends.utils.validateRunningStatus
import org.springframework.transaction.annotation.Transactional

open class GameServiceFinal(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao,
        private val questTimer: QuestTimer
) : GameService {

    override fun getCurrentTask(userId: Long): TeamState {
        val quest = gameDao.getLastQuestForUser(userId)?.takeIf { it.taskType == TaskType.MAIN }

        if (quest == null) {
            return TeamState.pause(text = "Основной этап начался! Капитан команды может взять первое задание.")
        }

        if (quest.status == QuestStatus.RUNNING) {
            return TeamState.play(quest)
        }

        val availableTaskIds = gameDao.getAvailableTasks(quest.teamId, TaskType.MAIN)
        if (availableTaskIds.isEmpty()) {
            return TeamState.stop("Поздравляем Вы прошли все задания! Легенды Бауманки 2019 завершены!")
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

        val taskStates = gameDao.getTaskStates(TaskType.MAIN)
        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(teamId)

        val nextTaskId = selectTask(taskStates, completedTaskIds) ?: return TeamState
                .stop("Поздравляем! Вы прошли все испытания, Легенды Бауманки 2019 завершены!")

        gameDao.startTask(teamId, nextTaskId)
        val quest = gameDao.getQuestOrThrow(teamId, nextTaskId)
        questTimer.startTimer(quest)

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

        quest.answers.find {
            answer.answer.equals(it, ignoreCase = true)
        } ?: return false

        questTimer.cancelTimer(quest)
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
        throw BadRequestException { "Нельзя пропустить задание основного этапа." }
    }

    private fun selectTask(
            allTasks: List<TaskState>,
            completedTaskIds: List<Long>
    ): Long? {

        return allTasks
                .filter { it.taskType == TaskType.MAIN && it.loadFactor < 1f }
                .toMutableList()
                .apply {
                    removeIf { completedTaskIds.contains(it.taskId) }
                    sortBy { it.hints }
                    sortBy { it.loadFactor }
                }
                .firstOrNull()?.taskId
    }
}