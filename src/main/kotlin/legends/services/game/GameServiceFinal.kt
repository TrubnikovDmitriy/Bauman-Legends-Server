package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.exceptions.BadRequestException
import legends.logic.QuestTimer
import legends.models.TaskState
import legends.models.TaskStatus
import legends.models.TaskType
import legends.models.TeamState
import org.springframework.transaction.annotation.Transactional

class GameServiceFinal(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao,
        private val questTimer: QuestTimer
) : GameService {

    override fun getCurrentTask(userId: Long): TeamState {
        val quest = gameDao.getLastQuestForUser(userId)

        if (quest == null) {
            return TeamState.pause(text = "Основной этап начался! Вы можете взять первое задание.")
        }

        if (quest.status == TaskStatus.RUNNING) {
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

        val allTasks = gameDao.getTasksActualStatus(TaskType.MAIN)
        val completedTaskIds = gameDao.getCompletedTaskIdsForTeam(teamId)

        val nextTaskId = selectTask(allTasks, completedTaskIds) ?: return TeamState
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
        if (user.teamId != answer.teamId) {
            throw BadRequestException { "Вы не состоите в команде №${answer.teamId}" }
        }

        val quest = gameDao.getQuest(teamId = answer.teamId, taskId = answer.taskId)
        if (quest?.status != TaskStatus.RUNNING) {
            throw BadRequestException { "Ваша команда в данный момент не выполняет задание №${answer.taskId}." }
        }

        quest.answers.find {
            answer.answer.equals(it, ignoreCase = true)
        } ?: return false

        questTimer.cancelTimer(quest)
        gameDao.finishTask(
                teamId = answer.teamId,
                taskId = answer.taskId,
                status = TaskStatus.SUCCESS,
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