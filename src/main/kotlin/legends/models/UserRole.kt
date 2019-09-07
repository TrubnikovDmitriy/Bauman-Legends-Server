package legends.models

enum class UserRole {
    ADMIN,
    MODERATOR,
    PLAYER,
    CAPTAIN,
    TESTER;

    companion object  {
        fun valueOfSafety(value: String): UserRole {
            return when(value.toUpperCase()) {
                ADMIN.name -> ADMIN
                MODERATOR.name -> MODERATOR
                PLAYER.name -> PLAYER
                CAPTAIN.name -> CAPTAIN
                TESTER.name -> TESTER
                else -> PLAYER
            }
        }
    }
}