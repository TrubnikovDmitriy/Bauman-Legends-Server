package legends.logic

import legends.exceptions.LegendsException
import legends.models.GameStatus
import legends.models.UserModel
import legends.models.UserRole
import org.springframework.http.HttpStatus


object GameState {

    @Volatile var status: GameStatus = GameStatus.REGISTRATION
        private set

    fun updateStatus(admin: UserModel, gameStatus: GameStatus) {
        if (admin.role != UserRole.ADMIN) {
            throw LegendsException(HttpStatus.FORBIDDEN)
            { "Только администратор может переключать этапы игры" }
        }
        status = gameStatus
    }

    @Deprecated("Backdoor for testing")
    fun updateStatusBackdoor(gameStatus: GameStatus) {
        status = gameStatus
    }
}