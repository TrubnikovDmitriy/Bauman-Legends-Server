package legends.contollers

import legends.dto.GameStageUpdate
import legends.logic.GameState
import legends.models.TaskType
import legends.services.ManageService
import legends.utils.getUserIdOrThrow
import legends.views.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/manage")
class ManageController(private val manageService: ManageService) {

    private val logger = LoggerFactory.getLogger(ManageController::class.java)

    @GetMapping("/traces")
    fun getTraces(
            httpSession: HttpSession,
            @RequestParam("complete") withCompleted: Boolean = false
    ): ResponseEntity<List<TraceView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get traces: moderatorId=[$userId]")
        val quests = manageService.getAllQuests(userId, withCompleted)
        val traceViews = quests.toTraceView(GameState.getMaxTaskCount())
        return ResponseEntity(traceViews, HttpStatus.OK)
    }

    @GetMapping("/tasks")
    fun getTraces(
            httpSession: HttpSession,
            @RequestParam("task_type") taskType: TaskType
    ): ResponseEntity<List<TaskStateView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get tasks: moderatorId=[$userId]")
        val taskStates = manageService.getTaskStates(userId, taskType)
        return ResponseEntity(taskStates.toView(), HttpStatus.OK)
    }

    @GetMapping("/quest")
    fun getTraces(
            httpSession: HttpSession,
            @RequestParam("team_id") teamId: Long
    ): ResponseEntity<TaskView> {
        val userId = httpSession.getUserIdOrThrow()
        val task = manageService.getTaskForTeam(userId, teamId)
        logger.info("Get quest for team: moderatorId=[$userId], task=[$task]")
        return ResponseEntity(TaskView(task), HttpStatus.OK)
    }

    // TODO: add QuestStatus in TeamModel
    @GetMapping("/teams")
    fun getTeams(
            httpSession: HttpSession
    ): ResponseEntity<List<TeamView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get teams: moderatorId=[$userId]")
        val teams = manageService.getAllTeams(userId)
        return ResponseEntity(teams.toView(), HttpStatus.OK)
    }

    @PostMapping("/stage")
    fun setStatus(
            httpSession: HttpSession,
            @RequestBody stage: GameStageUpdate
    ): ResponseEntity<Any> {
        val adminId = httpSession.getUserIdOrThrow()
        logger.warn("Change stage: adminId=[$adminId], stage=[$stage]")
        manageService.updateGameStage(adminId, stage)
        return ResponseEntity(HttpStatus.OK)
    }
}
