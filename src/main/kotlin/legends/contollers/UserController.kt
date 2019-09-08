package legends.contollers

import legends.dto.UserSignIn
import legends.dto.UserSignUp
import legends.exceptions.LegendsException
import legends.models.UserModel
import legends.views.ErrorView
import legends.services.UserService
import legends.utils.getUserId
import legends.utils.getUserIdOrThrow
import legends.utils.setUserId
import legends.views.UserView
import legends.views.toResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/sign_up")
    fun signUp(
            @RequestBody body: UserSignUp,
            httpSession: HttpSession
    ): ResponseEntity<UserView> {
        val userData = userService.signUp(body)
        httpSession.setUserId(userData.userId)
        return ResponseEntity(UserView(userData), HttpStatus.CREATED)
    }

    @PostMapping("/sign_in")
    fun signIn(
            @RequestBody body: UserSignIn,
            httpSession: HttpSession
    ): ResponseEntity<UserView> {

        val userData = userService.signIn(body)
        httpSession.setUserId(userData?.userId)

        return if (userData != null) {
            ResponseEntity(UserView(userData), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/info")
    fun getInfo(httpSession: HttpSession): ResponseEntity<UserView> {
        val userModel: UserModel? = httpSession.getUserId().let {
            userService.findUserById(it ?: return@let null)
        }
        return if (userModel != null) {
            ResponseEntity(UserView(userModel), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/logout")
    fun logout(httpSession: HttpSession): ResponseEntity<Any> {
        httpSession.invalidate()
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/delete")
    fun delete(httpSession: HttpSession): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()

        userService.deleteUser(userId)
        httpSession.invalidate()

        return ResponseEntity(HttpStatus.OK)
    }


    @ExceptionHandler(LegendsException::class)
    fun exceptionHandler(exception: LegendsException): ResponseEntity<ErrorView> {
        logger.warn("UserExceptionHandler ${exception.errorMessage()}")
        return exception.toResponse()
    }
}
