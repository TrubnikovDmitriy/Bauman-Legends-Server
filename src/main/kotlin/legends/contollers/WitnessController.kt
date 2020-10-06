package legends.contollers

import legends.dto.DecisionDto
import legends.dto.WitnessKeywordDto
import legends.services.WitnessService
import legends.utils.getUserIdOrThrow
import legends.views.DecisionView
import legends.views.WitnessView
import legends.views.toView
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/witness")
class WitnessController(private val witnessService: WitnessService) {

    private val logger = LoggerFactory.getLogger(WitnessController::class.java)

    @GetMapping("/inspect")
    fun getWitness(
            httpSession: HttpSession
    ): ResponseEntity<List<WitnessView>> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get ghosts: userId=[$userId]")
        val ghosts = witnessService.getWitnesses(userId)
        return ResponseEntity(ghosts.toView(), HttpStatus.OK)
    }

    @PostMapping("/inspect")
    fun inspectWitness(
            @RequestBody keyword: WitnessKeywordDto,
            httpSession: HttpSession
    ): ResponseEntity<WitnessView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Open ghost: userId=[$userId], keyword=[$keyword]")
        val witness = witnessService.openWitness(userId, keyword)
        return ResponseEntity(WitnessView(witness), HttpStatus.OK)
    }

    @GetMapping("/decision")
    fun getDecision(httpSession: HttpSession): ResponseEntity<DecisionView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get decision: userId=[$userId]")
        val text = witnessService.getDecision(userId)
        return ResponseEntity(DecisionView(text), HttpStatus.OK)
    }

    @PostMapping("/decision")
    fun setDecision(
            @RequestBody decision: DecisionDto,
            httpSession: HttpSession
    ): ResponseEntity<DecisionView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Set decision: userId=[$userId], freedom=[$decision]")
        val text = witnessService.setDecision(userId, decision.freedom)
        return ResponseEntity(DecisionView(text), HttpStatus.OK)
    }
}
