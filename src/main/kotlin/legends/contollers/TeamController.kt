package legends.contollers

import legends.dto.TeamJoin
import legends.dto.TeamSignUp
import legends.exceptions.LegendsException
import legends.exceptions.TeamIsNotPresented
import legends.services.TeamService
import legends.utils.getUserIdOrThrow
import legends.views.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/team")
class TeamController(private val teamService: TeamService) {

    private val logger = LoggerFactory.getLogger(TeamController::class.java)

    @PostMapping("/create")
    fun createTeam(
            @RequestBody team: TeamSignUp,
            httpSession: HttpSession
    ): ResponseEntity<TeamView> {
        val userId = httpSession.getUserIdOrThrow()
        val teamData = teamService.createTeam(userId, team)
        val teamView = TeamView(userId, teamData)
        return ResponseEntity(teamView, HttpStatus.CREATED)
    }

    @GetMapping("/info")
    fun getTeam(httpSession: HttpSession): ResponseEntity<*> {
        val userId = httpSession.getUserIdOrThrow()
        val teamData = teamService.getTeamByUserId(userId)
        return if (teamData != null) {
            val teamView = TeamView(userId, teamData)
            ResponseEntity(teamView, HttpStatus.OK)
        } else {
            TeamIsNotPresented().toResponse()
        }
    }

    @GetMapping("/members")
    fun getMembers(httpSession: HttpSession): ResponseEntity<List<UserView>> {
        val userId = httpSession.getUserIdOrThrow()
        val userList = teamService.getTeammates(userId)
        return ResponseEntity(userList.toView(), HttpStatus.OK)
    }

    @GetMapping("/all")
    fun getAllTeams(): ResponseEntity<List<TeamView>> {
        val teams = teamService.getAllTeams()
        return ResponseEntity(teams.toView(), HttpStatus.OK)
    }

    @PostMapping("/join")
    fun joinToTeam(
            @RequestBody join: TeamJoin,
            httpSession: HttpSession
    ): ResponseEntity<TeamView> {
        val userId = httpSession.getUserIdOrThrow()
        val team = teamService.joinUserToTeam(userId, join)
        return ResponseEntity(TeamView(team), HttpStatus.OK)
    }

    @DeleteMapping("/leave")
    fun leaveTeam(httpSession: HttpSession): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        teamService.selfKick(userId)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/kick")
    fun kick(
            @RequestParam(required = true, value = "user_id") kickId: Long,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        teamService.kickUser(userId, kickId)
        return ResponseEntity(HttpStatus.OK)
    }


    @ExceptionHandler(LegendsException::class)
    fun exceptionHandler(exception: LegendsException): ResponseEntity<ErrorView> {
        logger.warn("TeamExceptionHandler ${exception.errorMessage()}")
        return exception.toResponse()
    }
}
