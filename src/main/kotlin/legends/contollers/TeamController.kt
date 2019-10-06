package legends.contollers

import legends.dto.TeamJoin
import legends.dto.TeamSignUp
import legends.exceptions.TeamIsNotPresented
import legends.services.TeamService
import legends.utils.getUserIdOrThrow
import legends.views.TeamView
import legends.views.toResponse
import legends.views.toView
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
        logger.trace("Create team: userId=[$userId], teamName=[${team.teamName}]")

        val teamData = teamService.createTeam(userId, team)
        val teamView = TeamView(userId, teamData)

        return ResponseEntity(teamView, HttpStatus.CREATED)
    }

    @PostMapping("/update")
    fun updateTeam(
            @RequestBody team: TeamSignUp,
            httpSession: HttpSession
    ): ResponseEntity<TeamView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Update team: userId=[$userId], teamName=[${team.teamName}]")

        val teamData = teamService.updateTeamName(userId, team)
        val teamView = TeamView(userId, teamData)

        return ResponseEntity(teamView, HttpStatus.OK)
    }

    @GetMapping("/info")
    fun getTeam(httpSession: HttpSession): ResponseEntity<*> {
        val userId = httpSession.getUserIdOrThrow()
        val teamData = teamService.getTeamByUserId(userId)
        logger.info("Get team info: userId=[$userId], team=[$teamData]")

        return if (teamData != null) {
            val teamView = TeamView(userId, teamData)
            ResponseEntity(teamView, HttpStatus.OK)
        } else {
            TeamIsNotPresented().toResponse()
        }
    }

    @GetMapping("/members")
    fun getMembers(httpSession: HttpSession): ResponseEntity<*> {
        val userId = httpSession.getUserIdOrThrow()
        val userList = teamService.getTeammates(userId)
        logger.info("Get members of team: userId=[$userId], members=[${userList?.size}]")

        return if (userList != null) {
            ResponseEntity(userList.toView(), HttpStatus.OK)
        } else {
            TeamIsNotPresented().toResponse()
        }
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
        logger.info("Join to team: userId=[$userId], join=[$join]")
        val team = teamService.joinUserToTeam(userId, join)

        return ResponseEntity(TeamView(team), HttpStatus.OK)
    }

    @DeleteMapping("/leave")
    fun leaveTeam(httpSession: HttpSession): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Leave team: userId=[$userId]")
        teamService.selfKick(userId)
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/kick")
    fun kick(
            @RequestParam(required = true, value = "user_id") kickId: Long,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Kick from team: captainId=[$userId], kickId=[$kickId]")
        teamService.kickUser(userId, kickId)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/change")
    fun changeCaptain(
            @RequestParam(required = true, value = "new_captain") newCaptainId: Long,
            httpSession: HttpSession
    ): ResponseEntity<TeamView> {
        val oldCaptainId = httpSession.getUserIdOrThrow()
        logger.info("Change captain: oldCaptainId=[$oldCaptainId], newCaptainId=[$newCaptainId]")
        val teamData = teamService.changePartyLeader(oldCaptainId, newCaptainId)
        return ResponseEntity(TeamView(oldCaptainId, teamData), HttpStatus.OK)
    }
}
