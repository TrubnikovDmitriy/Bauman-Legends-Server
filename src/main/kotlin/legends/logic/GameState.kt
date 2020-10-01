package legends.logic

import legends.models.GameStage

object GameState {

    @Volatile var stage: GameStage = GameStage.REGISTRATION
        private set

    const val MAX_FINAL_TASK_COUNT: Int = 10
    const val MAX_PILOT_TASK_COUNT: Int = 6

    const val SCORE_PER_GHOST = 100

    fun getMaxTaskCount(): Int {
        return when(stage) {
            GameStage.REGISTRATION -> 0
            GameStage.PILOT -> MAX_PILOT_TASK_COUNT
            GameStage.FINAL -> MAX_FINAL_TASK_COUNT
            GameStage.FINISH -> Int.MAX_VALUE
        }
    }

    fun updateStage(gameStage: GameStage) {
        stage = gameStage
    }
}