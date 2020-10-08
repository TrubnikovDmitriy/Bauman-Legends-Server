package legends.contollers

import legends.services.BaumanService
import legends.utils.getUserIdOrThrow
import legends.views.BaumanFragmentsView
import legends.views.toView
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/bauman")
class BaumanController(private val baumanService: BaumanService) {

    @GetMapping("/fragments")
    fun getBaumanFragments(httpSession: HttpSession): ResponseEntity<List<BaumanFragmentsView>>  {
        val userId = httpSession.getUserIdOrThrow()
        val baumanFragments = baumanService.getBaumanFragments(userId)
        return ResponseEntity(baumanFragments.toView(), HttpStatus.OK)
    }
}