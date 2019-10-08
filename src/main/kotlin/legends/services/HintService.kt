package legends.services

import legends.dao.HintDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.HintDto
import legends.exceptions.BadRequestException
import legends.models.HintModel
import legends.models.OpenHintModel
import legends.utils.ValidationUtils
import legends.utils.ValidationUtils.INVALID_ID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HintService(
        private val hintDao: HintDao,
        private val teamDao: TeamDao,
        private val userDao: UserDao
) {

    fun createHint(userId: Long, dto: HintDto): HintModel {
        userDao.getUserOrThrow(userId).checkModerator()

        val reason = ValidationUtils.validateHint(dto)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        val hint = dto.convert(id = INVALID_ID)
        val taskId = hintDao.createHint(hint)

        return hint.copy(taskId = taskId)
    }

    @Transactional
    fun updateHint(userId: Long, dto: HintDto): HintModel {
        userDao.getUserOrThrow(userId).checkModerator()
        if (dto.hintId == null) {
            throw BadRequestException { "Не указан номер обновляемой подсказки" }
        }

        val reason = ValidationUtils.validateHint(dto)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        val newHint = dto.convert(id = dto.hintId)
        hintDao.updateHint(newHint)

        return newHint
    }

    @Transactional
    fun buyHintForTeam(captainId: Long, hintId: Long): HintModel {
        val teamId = userDao.getUserOrThrow(captainId).checkCaptain()
        val hint = hintDao.getHintOrThrow(hintId)

        teamDao.decreaseScore(teamId, hint.cost)
        hintDao.openHintForTeam(hintId, teamId)

        return hint
    }

    fun getHintsForTeam(userId: Long): List<OpenHintModel> {
        val user = userDao.getUserOrThrow(userId)
        if (user.teamId == null) {
            throw BadRequestException { "Вы не состоите в команде." }
        }
        return hintDao.getHintsForTeam(user.teamId)
    }

    fun getAllHints(userId: Long): List<HintModel> {
        userDao.getUserOrThrow(userId).checkModerator()
        return hintDao.getAllHints()
    }

    @Transactional
    fun deleteHint(userId: Long, hintId: Long) {
        userDao.getUserOrThrow(userId).checkModerator()
        hintDao.deleteHint(hintId)
    }
}