package legends.contollers

import legends.dto.GhostKeywordDto
import legends.services.GhostService
import legends.utils.getUserIdOrThrow
import legends.views.GhostView
import legends.views.toView
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/ghost")
class GhostController(private val ghostService: GhostService) {

    private val logger = LoggerFactory.getLogger(GhostController::class.java)

    @GetMapping
    fun getGhosts(
            httpSession: HttpSession
    ): ResponseEntity<List<GhostView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get ghosts: userId=[$userId]")
        val ghosts = ghostService.getGhosts(userId)
        return ResponseEntity(ghosts.toView(), HttpStatus.OK)
    }

    @PostMapping
    fun openGhost(
            @RequestBody keyword: GhostKeywordDto,
            httpSession: HttpSession
    ): ResponseEntity<GhostView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Open ghost: userId=[$userId], keyword=[$keyword]")
        val ghost = ghostService.openGhost(userId, keyword)
        return ResponseEntity(GhostView(ghost), HttpStatus.OK)
    }
}
