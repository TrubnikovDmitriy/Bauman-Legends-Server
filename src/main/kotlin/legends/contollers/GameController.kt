package legends.contollers

import legends.dto.AnswerDto
import legends.dto.GameStateUpdate
import legends.logic.GameState
import legends.services.game.GameService
import legends.utils.getUserIdOrThrow
import legends.views.ErrorView
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
    fun status(): ResponseEntity<String> = ResponseEntity(GameState.status.name, HttpStatus.OK)

    @PostMapping("/status")
    fun setStatus(
            httpSession: HttpSession,
            @RequestBody state: GameStateUpdate
    ): ResponseEntity<Any> {
        GameState.updateStatusBackdoor(state.status)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/info")
    fun info(httpSession: HttpSession): ResponseEntity<TeamStateView> {
        val userId = httpSession.getUserIdOrThrow()
        val currentTask = gameService.getCurrentTask(userId)
        return ResponseEntity(TeamStateView(currentTask), HttpStatus.OK)
    }

    @GetMapping("/next")
    fun start(httpSession: HttpSession): ResponseEntity<TeamStateView> {
        val userId = httpSession.getUserIdOrThrow()
        val nextTask = gameService.startNextTask(userId)
        return ResponseEntity(TeamStateView(nextTask), HttpStatus.OK)
    }

    @PostMapping("/answer")
    fun tryAnswer(
            @RequestBody answer: AnswerDto,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
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
        gameService.skipTask(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}
