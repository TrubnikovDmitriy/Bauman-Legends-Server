package legends.services

import legends.dao.FeedbackDao
import legends.dao.GhostDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.FeedbackDto
import legends.dto.GhostKeywordDto
import legends.exceptions.BadRequestException
import legends.exceptions.TeamIsNotPresented
import legends.logic.GameState
import legends.models.GhostModel
import legends.utils.ValidationUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedbackService(
        private val feedbackDao: FeedbackDao,
        private val userDao: UserDao
) {

    fun saveFeedback(userId: Long, dto: FeedbackDto) {
        val reason = ValidationUtils.validateFeedback(dto)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        val feedback = dto.convert(userId)
        feedbackDao.saveFeedback(feedback)
    }

    fun isFeedbackExists(userId: Long): Boolean = feedbackDao.isUserFeedbackExists(userId)
}