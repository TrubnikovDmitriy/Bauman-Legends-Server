package legends.contollers

import legends.dto.AnswerDto
import legends.logic.GameState
import legends.services.game.GameService
import legends.utils.getUserIdOrThrow
import legends.views.ErrorView
import legends.views.FactView
import legends.views.TeamStateView
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/game")
class GameController(private val gameService: GameService) {

    private val logger = LoggerFactory.getLogger(GameController::class.java)

    @GetMapping("/status")
    fun status(): ResponseEntity<String> = ResponseEntity(GameState.stage.name, HttpStatus.OK)

    @GetMapping("/info")
    fun currentQuest(httpSession: HttpSession): ResponseEntity<TeamStateView> {
        val userId = httpSession.getUserIdOrThrow()
        val currentTask = gameService.getCurrentTask(userId)
        logger.info("""Get current quest: 
            status=[${currentTask.status}], 
            task=[${currentTask.quest?.taskName}], 
            text=[${currentTask.text}]
        """.trimIndent())
        return ResponseEntity(TeamStateView(currentTask), HttpStatus.OK)
    }

    @GetMapping("/next")
    fun start(httpSession: HttpSession): ResponseEntity<TeamStateView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Take next quest: captainId=[$userId]")
        val nextTask = gameService.startNextTask(userId)
        return ResponseEntity(TeamStateView(nextTask), HttpStatus.OK)
    }

    @PostMapping("/answer")
    fun tryAnswer(
            @RequestBody answer: AnswerDto,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Try answer: [$answer]")
        val isCorrect = gameService.tryAnswer(userId, answer)
        return if (isCorrect) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(ErrorView("Ответ \"${answer.answer}\" не подходит."), HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/skip")
    fun skip(httpSession: HttpSession): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Skip quest: [$userId]")
        gameService.skipTask(userId)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/fact")
    fun getFact(
            httpSession: HttpSession
    ): ResponseEntity<FactView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Get fact: userId=[$userId]")
        val fact = gameService.getFact(userId)
        return ResponseEntity(FactView(fact), HttpStatus.OK)
    }
}
