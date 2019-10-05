package legends.models

enum class UserRole {
    ADMIN,
    MODERATOR,
    PLAYER,
    CAPTAIN,
    REVISOR;

    companion object  {
        fun valueOfSafety(value: String): UserRole {
            return when(value.toUpperCase()) {
                ADMIN.name -> ADMIN
                MODERATOR.name -> MODERATOR
                PLAYER.name -> PLAYER
                CAPTAIN.name -> CAPTAIN
                REVISOR.name -> REVISOR
                else -> PLAYER
            }
        }
    }
}