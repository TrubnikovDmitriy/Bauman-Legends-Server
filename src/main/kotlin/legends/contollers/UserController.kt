package legends.contollers

import legends.dto.UserSignIn
import legends.dto.UserSignUp
import legends.dto.UserUpdate
import legends.models.UserModel
import legends.services.UserService
import legends.utils.getUserId
import legends.utils.getUserIdOrThrow
import legends.utils.setUserId
import legends.views.ErrorView
import legends.views.UserView
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
        logger.trace("Sign up: firstName=[${body.firstName}], lastName=[${body.lastName}], login=[${body.login}]")
        val userData = userService.signUp(body)
        httpSession.setUserId(userData.userId)
        return ResponseEntity(UserView(userData), HttpStatus.CREATED)
    }

    @PostMapping("/sign_in")
    fun signIn(
            @RequestBody body: UserSignIn,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        logger.info("Sign in: login=[${body.login}]")

        val user = userService.signIn(body)
        httpSession.setUserId(user?.userId)

        return if (user != null) {
            ResponseEntity(UserView(user), HttpStatus.OK)
        } else {
            ResponseEntity(ErrorView("Неверный логин или пароль"), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/update")
    fun updateProfile(
            @RequestBody body: UserUpdate,
            httpSession: HttpSession
    ): ResponseEntity<UserView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Update profile: userId=[$userId], updateData=[$body]")
        val user = userService.updateProfile(userId, body)
        return ResponseEntity(UserView(user), HttpStatus.OK)
    }

    @GetMapping("/info")
    fun getInfo(httpSession: HttpSession): ResponseEntity<UserView> {
        val userModel: UserModel? = httpSession.getUserId()?.let {
            userService.findUserById(it)
        }
        logger.info("Get profile: userId=[${userModel?.userId}], login=[${userModel?.login}]")

        return if (userModel != null) {
            ResponseEntity(UserView(userModel), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/logout")
    fun logout(httpSession: HttpSession): ResponseEntity<Any> {
        logger.info("Log out: userId=[${httpSession.getUserId()}]")
        httpSession.invalidate()
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("/delete")
    fun delete(httpSession: HttpSession): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Delete account: userId=[$userId]")

        userService.deleteUser(userId)
        httpSession.invalidate()

        return ResponseEntity(HttpStatus.OK)
    }
}
