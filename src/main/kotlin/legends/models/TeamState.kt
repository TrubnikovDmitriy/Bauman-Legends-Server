package legends.models

data class TeamState(
        val status: TeamStatus,
        val quest: QuestModel?,
        val text: String?
) {
    companion object {
        fun play(quest: QuestModel): TeamState {
            return TeamState(
                    status = TeamStatus.PLAY,
                    quest = quest,
                    text = null
            )
        }
        fun stop(text: String): TeamState {
            return TeamState(
                    status = TeamStatus.STOP,
                    quest = null,
                    text = text
            )
        }
        fun pause(quest: QuestModel? = null, text: String? = null): TeamState {
            return TeamState(
                    status = TeamStatus.PAUSE,
                    quest = quest,
                    text = text
            )
        }
    }
}