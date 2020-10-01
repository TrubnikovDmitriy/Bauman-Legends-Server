package legends.models

data class TeamState(
        val status: TeamStatus,
        val quest: QuestModel?,
        val text: String?,
        val attempts: Int?
) {
    companion object {
        fun play(quest: QuestModel, attempts: Int): TeamState {
            return TeamState(
                    status = TeamStatus.PLAY,
                    quest = quest,
                    text = null,
                    attempts = attempts
            )
        }
        fun stop(text: String): TeamState {
            return TeamState(
                    status = TeamStatus.STOP,
                    quest = null,
                    text = text,
                    attempts = null
            )
        }
        fun pause(
                quest: QuestModel? = null,
                text: String? = null,
                attempts: Int? = null
        ): TeamState {
            return TeamState(
                    status = TeamStatus.PAUSE,
                    quest = quest,
                    text = text,
                    attempts = attempts
            )
        }
    }
}