package legends.models

enum class QuestStatus {
    RUNNING,
    SUCCESS,
    FAIL,
    SKIP;

    companion object  {
        fun valueOfSafety(value: String): QuestStatus {
            return when(value.toUpperCase()) {
                RUNNING.name -> RUNNING
                SUCCESS.name -> SUCCESS
                FAIL.name -> FAIL
                SKIP.name -> SKIP
                else -> SKIP
            }
        }
    }
}