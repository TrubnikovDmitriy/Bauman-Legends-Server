package legends.logic

import legends.exceptions.LegendsException
import legends.models.GameStage
import legends.models.UserModel
import legends.models.UserRole
import org.springframework.http.HttpStatus


object GameState {

    @Volatile var stage: GameStage = GameStage.REGISTRATION
        private set

    @Volatile private var maxFinalTaskCount: Int = 10
    @Volatile private var maxPilotTaskCount: Int = 6

    fun getMaxTaskCount(): Int {
        return when(stage) {
            GameStage.REGISTRATION -> 0
            GameStage.PILOT -> maxPilotTaskCount
            GameStage.FINAL -> maxFinalTaskCount
            GameStage.FINISH -> Int.MAX_VALUE
        }
    }

    fun updateStatus(admin: UserModel, gameStage: GameStage) {
        if (admin.role != UserRole.ADMIN) {
            throw LegendsException(HttpStatus.FORBIDDEN)
            { "Только администратор может переключать этапы игры" }
        }
        stage = gameStage
    }

    @Deprecated("Backdoor for testing")
    fun updateStatusBackdoor(gameStage: GameStage) {
        stage = gameStage
    }
}