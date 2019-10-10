package legends.services

import legends.dao.GhostDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.GhostKeywordDto
import legends.exceptions.BadRequestException
import legends.exceptions.TeamIsNotPresented
import legends.logic.GameState
import legends.models.GhostModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GhostService(
        private val ghostDao: GhostDao,
        private val teamDao: TeamDao,
        private val userDao: UserDao
) {

    fun getGhosts(userId: Long): List<GhostModel> {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        return ghostDao.getGhostsForTeam(teamId)
    }

    @Transactional
    fun openGhost(userId: Long, ghostKeyword: GhostKeywordDto): GhostModel {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        val ghost = ghostDao.getGhostByKeyword(ghostKeyword.keyword) ?: throw BadRequestException {
            "Не удалось найти такого призрака."
        }
        ghostDao.openGhost(teamId, ghost.ghostId)
        teamDao.increaseScore(teamId, GameState.SCORE_PER_GHOST)
        return ghost
    }
}