package legends.contollers

import legends.dto.FeedbackDto
import legends.dto.GhostKeywordDto
import legends.services.FeedbackService
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
@RequestMapping("/feedback")
class FeedbackController(private val feedbackService: FeedbackService) {

    private val logger = LoggerFactory.getLogger(FeedbackController::class.java)

    @PostMapping
    fun saveFeedback(
            httpSession: HttpSession,
            @RequestBody feedback: FeedbackDto
    ): ResponseEntity<List<GhostView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Save feedback: userId=[$userId]")
        feedbackService.saveFeedback(userId, feedback)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @GetMapping
    fun isExists(
            httpSession: HttpSession
    ): ResponseEntity<Boolean> {
        val userId = httpSession.getUserIdOrThrow()
        logger.debug("Is feedback exists: userId=[$userId]")
        val isExists = feedbackService.isFeedbackExists(userId)
        return ResponseEntity(isExists, HttpStatus.OK)
    }
}
