package legends.logic

import legends.exceptions.LegendsException
import legends.models.GameStatus
import legends.models.UserModel
import legends.models.UserRole
import org.springframework.http.HttpStatus


object GameState {

    @Volatile var status: GameStatus = GameStatus.REGISTRATION
        private set

    @Volatile private var maxFinalTaskCount: Int = 10
    @Volatile private var maxPilotTaskCount: Int = 6

    fun getMaxTaskCount(): Int {
        return when(status) {
            GameStatus.REGISTRATION -> 0
            GameStatus.PILOT -> maxPilotTaskCount
            GameStatus.FINAL -> maxFinalTaskCount
            GameStatus.FINISH -> Int.MAX_VALUE
        }
    }

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