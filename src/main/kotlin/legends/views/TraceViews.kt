package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.QuestModel
import legends.models.TaskStatus
import java.util.*

/**
 * Вьюха для "кругляшей" в админке модераторов,
 * из которых строится текущий маршрут команды.
 */
data class TraceCircleView(
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("task_name") val taskName: String,
        @JsonProperty("duration") val duration: Long?, // seconds
        @JsonProperty("start_time") val startTime: Long, // seconds
        @JsonProperty("finish_time") val finishTime: Long?, // seconds
        @JsonProperty("task_status") val taskStatus: TaskStatus
) {
    constructor(quest: QuestModel) : this(
            taskId = quest.taskId,
            taskName = quest.taskName,
            duration = quest.duration,
            startTime = quest.startTime,
            finishTime = quest.finishTime,
            taskStatus = quest.status
    )
}

data class TraceView(
        @JsonProperty("team_id") val teamId: Long,
        @JsonProperty("circles") val circle: List<TraceCircleView>,
        @JsonProperty("complete") val complete: Boolean
)


fun Map<Long, List<QuestModel>>.toTraceView(maxTaskCount: Int): List<TraceView> {
    val traceViews = LinkedList<TraceView>()
    for ((teamId, quests) in this) {
        traceViews.add(TraceView(
                teamId = teamId,
                circle = quests.map { TraceCircleView(it) },
                complete = quests.size == maxTaskCount
        ))
    }
    return traceViews
}


