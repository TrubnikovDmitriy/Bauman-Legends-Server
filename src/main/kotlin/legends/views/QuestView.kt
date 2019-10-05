package legends.views

import com.fasterxml.jackson.annotation.JsonProperty
import legends.models.QuestModel
import legends.models.QuestStatus
import legends.models.TaskType

data class QuestView (
        @JsonProperty("task_id") val taskId: Long,
        @JsonProperty("html") val html: String,
        @JsonProperty("task_type") val taskType: TaskType,
        @JsonProperty("duration") val duration: Long?, // seconds
        @JsonProperty("points") val points: Int,
        @JsonProperty("skip") val skip: Boolean,
        @JsonProperty("start_time") val startTime: Long, // seconds
        @JsonProperty("finish_time") val finishTime: Long?, // seconds
        @JsonProperty("answer") val answer: String?,
        @JsonProperty("task_status") val questStatus: QuestStatus
) {
    constructor(quest: QuestModel) : this(
            taskId = quest.taskId,
            html = quest.html,
            taskType = quest.taskType,
            duration = quest.duration,
            points = quest.points,
            skip = quest.skipPossible,
            startTime = quest.startTime,
            finishTime = quest.finishTime,
            answer = quest.answer,
            questStatus = quest.status
    )
}
