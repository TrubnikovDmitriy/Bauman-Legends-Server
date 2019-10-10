package legends.logic

import legends.models.GameStage

object GameState {

    @Volatile var stage: GameStage = GameStage.REGISTRATION
        private set

    private const val maxFinalTaskCount: Int = 10
    private const val maxPilotTaskCount: Int = 6

    const val SCORE_PER_GHOST = 100

    fun getMaxTaskCount(): Int {
        return when(stage) {
            GameStage.REGISTRATION -> 0
            GameStage.PILOT -> maxPilotTaskCount
            GameStage.FINAL -> maxFinalTaskCount
            GameStage.FINISH -> Int.MAX_VALUE
        }
    }

    fun updateStage(gameStage: GameStage) {
        stage = gameStage
    }
}