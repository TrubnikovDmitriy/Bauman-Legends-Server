package legends.contollers

import legends.logic.GameState
import legends.models.TaskType
import legends.services.ModeratorService
import legends.utils.getUserIdOrThrow
import legends.views.TaskStateView
import legends.views.TraceView
import legends.views.toTraceView
import legends.views.toView
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/moderator")
class ModeratorController(private val moderatorService: ModeratorService) {

    private val logger = LoggerFactory.getLogger(ModeratorController::class.java)

    @GetMapping("/traces")
    fun getTraces(
            httpSession: HttpSession,
            @RequestParam("complete") withCompleted: Boolean = false
    ): ResponseEntity<List<TraceView>> {
        val userId = httpSession.getUserIdOrThrow()
        val quests = moderatorService.getAllQuests(userId, withCompleted)
        val traceViews = quests.toTraceView(GameState.getMaxTaskCount())
        return ResponseEntity(traceViews, HttpStatus.OK)
    }

    @GetMapping("/tasks")
    fun getTraces(
            httpSession: HttpSession,
            @RequestParam("task_type") taskType: TaskType
    ): ResponseEntity<List<TaskStateView>> {
        val userId = httpSession.getUserIdOrThrow()
        val taskStates = moderatorService.getTaskStates(userId, taskType)
        return ResponseEntity(taskStates.toView(), HttpStatus.OK)
    }

    // TODO: add QuestStatus in TeamModel
//    @GetMapping("/teams")
//    fun getTeams(
//            httpSession: HttpSession
//    ): ResponseEntity<List<TraceView>> {
//        val userId = httpSession.getUserIdOrThrow()
//        val teams = moderatorService.getAllTeams(userId)
//        return ResponseEntity(teams.toView(), HttpStatus.OK)
//    }
}
