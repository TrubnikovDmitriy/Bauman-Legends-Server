package legends.utils

import legends.exceptions.LegendsException
import org.springframework.http.HttpStatus
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpSession

private const val ATTR_USER_ID = "ATTR_USER_ID"

fun HttpSession.getUserIdOrThrow(): Long {
    val userId = getAttribute(ATTR_USER_ID) as Long?
    if (userId != null) {
        return userId
    }
    throw LegendsException(HttpStatus.UNAUTHORIZED)
    { "Для продолжения работы необходимо зарегистрироваться" }
}

fun HttpSession.getUserId(): Long? =
        getAttribute(ATTR_USER_ID) as Long?

fun HttpSession.setUserId(userId: Long?) {
    setAttribute(ATTR_USER_ID, userId)
    maxInactiveInterval = TimeUnit.HOURS.toSeconds(4).toInt()
}
