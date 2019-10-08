package legends.contollers

import legends.dto.HintDto
import legends.services.HintService
import legends.utils.getUserIdOrThrow
import legends.views.GroupHintsView
import legends.views.HintView
import legends.views.toGroupView
import legends.views.toPlayerView
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/hint")
class HintController(private val hintService: HintService) {

    private val logger = LoggerFactory.getLogger(HintController::class.java)

    @PostMapping
    fun createHint(
            @RequestBody hintDto: HintDto,
            httpSession: HttpSession
    ): ResponseEntity<HintView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Create hint: userId=[$userId], hint=[$hintDto]")
        val hint = hintService.createHint(userId, hintDto)
        return ResponseEntity(HintView(hint), HttpStatus.OK)
    }

    @PutMapping
    fun updateHint(
            @RequestBody hintDto: HintDto,
            httpSession: HttpSession
    ): ResponseEntity<HintView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Update hint: userId=[$userId], hint=[$hintDto]")
        val hint = hintService.updateHint(userId, hintDto)
        return ResponseEntity(HintView(hint), HttpStatus.OK)
    }

    @GetMapping("/buy")
    fun getHintsForTeam(
            @RequestParam(required = true, value = "hint_id") hintId: Long,
            httpSession: HttpSession
    ): ResponseEntity<HintView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Buy hint: userId=[$userId], hintId=[$hintId]")
        val hint = hintService.buyHintForTeam(userId, hintId)
        return ResponseEntity(HintView(hint), HttpStatus.OK)
    }

    @GetMapping("/team")
    fun getHintsForTeam(
            httpSession: HttpSession
    ): ResponseEntity<List<HintView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get hints for team: userId=[$userId]")
        val hints = hintService.getHintsForTeam(userId)
        return ResponseEntity(hints.toPlayerView(), HttpStatus.OK)
    }

    @GetMapping("/all")
    fun updateHint(
            httpSession: HttpSession
    ): ResponseEntity<List<GroupHintsView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Get all hints: userId=[$userId]")
        val hints = hintService.getAllHints(userId)
        return ResponseEntity(hints.toGroupView(), HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteHint(
            @RequestParam(required = true, value = "hint_id") hintId: Long,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Delete hint: userId=[$userId], hint=[$hintId]")
        hintService.deleteHint(userId, hintId)
        return ResponseEntity(HttpStatus.OK)
    }
}
