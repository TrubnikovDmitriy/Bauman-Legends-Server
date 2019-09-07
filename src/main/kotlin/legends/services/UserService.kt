package legends.services

import legends.dao.UserDao
import legends.dto.UserSignIn
import legends.dto.UserSignUp
import legends.exceptions.BadRequestException
import legends.exceptions.LegendsException
import legends.models.UserModel
import legends.models.UserRole
import legends.utils.SecureUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class UserService(private val userDao: UserDao) {

    companion object {
        private const val INVALID_USER_ID = -1L
    }

    private val secureUtils = SecureUtils()

    fun signUp(dto: UserSignUp): UserModel {
        if (!dto.isValid()) {
            throw BadRequestException { "Форма регистрации заполнена неправильно" }
        }

        val salt = secureUtils.generateSalt()
        val hash = secureUtils.getHash(dto.password, salt)

        val user = UserModel(
                userId = INVALID_USER_ID,
                login = dto.login,
                role = UserRole.PLAYER,
                teamId = null,
                firstName = dto.firstName,
                lastName = dto.lastName,
                group = dto.group.toUpperCase(),
                vkRef = dto.vkRef,
                hashedPassword = hash,
                salt = salt
        )
        val userId = userDao.insertUser(user)

        return user.copy(userId = userId)
    }

    fun signIn(dto: UserSignIn): UserModel? {
        val user = userDao.getUserByLogin(dto.login) ?: return null
        val hash = secureUtils.getHash(dto.password, user.salt)

        return if (user.hashedPassword.contentEquals(hash))
            user
        else
            null
    }

    fun findUserById(userId: Long): UserModel? {
        return userDao.getUserById(userId)
    }

    fun deleteUser(userId: Long) {
        val user = userDao.getUserOrThrow(userId)
        if (user.role == UserRole.CAPTAIN) {
            throw BadRequestException { "Вы не можете удалить аккаунт, так как являетесь капитаном команды №${user.teamId}" }
        }
        userDao.deleteUser(userId)
    }
}