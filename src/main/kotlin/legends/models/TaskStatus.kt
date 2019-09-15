package legends.models

enum class TaskStatus {
    RUNNING,
    SUCCESS,
    FAIL,
    SKIP;

    companion object  {
        fun valueOfSafety(value: String): TaskStatus {
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