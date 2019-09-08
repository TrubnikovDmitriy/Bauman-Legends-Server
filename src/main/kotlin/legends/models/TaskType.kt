package legends.models

enum class TaskType {
    PHOTO,
    LOGIC,
    MAIN,
    DRAFT;

    companion object  {
        fun valueOfSafety(value: String): TaskType {
            return when(value.toUpperCase()) {
                PHOTO.name -> PHOTO
                LOGIC.name -> LOGIC
                MAIN.name -> MAIN
                else -> DRAFT
            }
        }
    }
}