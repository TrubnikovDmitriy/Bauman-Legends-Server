package legends.models

data class FeedbackModel(
        val userId: Long,
        val pilotMark: Int,
        val finalMark: Int,
        val legendsMark: Int,
        val siteMark: Int,
        val taskMark: Int,
        val ghostMark: Int,
        val bestTask: String,
        val worstTask: String,
        val from: String,
        val message: String
)