package legends.logic

import legends.dao.GameDao
import legends.exceptions.BadRequestException
import legends.models.GameStage
import legends.models.QuestModel
import legends.models.QuestStatus
import legends.models.TaskType
import legends.utils.TimeUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Component
class QuestTimer(private val gameDao: GameDao) {

    private data class QuestId(
            val teamId: Long,
            val taskId: Long
    ) {
        constructor(quest: QuestModel) : this(
                quest.teamId,
                quest.taskId
        )
    }

    private val logger = LoggerFactory.getLogger(QuestTimer::class.java)

    private val scheduler = Executors.newScheduledThreadPool(1)
    private val questTimers = ConcurrentHashMap<QuestId, ScheduledFuture<*>>()


    init {
        if (GameState.stage == GameStage.FINAL) {
            // Reschedule running quests after server reboot
            val runningQuests = gameDao.getAllRunningQuests().filter { it.taskType == TaskType.MAIN }
            logger.info("Number of running quests: [${runningQuests.size}]")

            for (quest in runningQuests) {
                startTimer(quest)
            }
        }
    }


    final fun startTimer(quest: QuestModel) {
        quest.duration ?: return
        val questId = QuestId(quest)

        val finishTime = quest.startTime + quest.duration
        val currentTime = TimeUtils.currentTime(TimeUnit.SECONDS)
        val delay = finishTime - currentTime

        logger.info("Start timer for [$questId] with delay [$delay] seconds")
        val future = scheduler.schedule({ finishQuest(questId) }, delay, TimeUnit.SECONDS)

        questTimers[questId] = future
    }

    final fun cancelTimer(quest: QuestModel) {
        val questId = QuestId(quest)
        logger.info("Cancel timer for [$questId]")
        questTimers.remove(questId)?.cancel(false)
    }


    private fun finishQuest(quest: QuestId) {
        try {
            logger.info("Force stop quest [$quest]")
            gameDao.finishTask(
                    teamId = quest.teamId,
                    taskId = quest.taskId,
                    status = QuestStatus.FAIL
            )
        } catch (e: BadRequestException) {
            logger.warn("Fail to force stop the quest", e)
        }
    }
}
