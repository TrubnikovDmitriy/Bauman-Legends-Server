package legends.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class SecureUtils {
    companion object {
        private const val SALT_LENGTH = 16
        private const val ALGORITHM_NAME = "SHA-512"
    }

    private val random = SecureRandom(Calendar.getInstance().timeInMillis.toString().toByteArray())

    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    fun getHash(password: String, salt: ByteArray): ByteArray {
        return MessageDigest.getInstance(ALGORITHM_NAME).run {
            update(salt)
            digest(password.toByteArray())
        }
    }
}