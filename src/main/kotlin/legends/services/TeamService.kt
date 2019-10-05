package legends.services

import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.TeamJoin
import legends.dto.TeamSignUp
import legends.exceptions.BadRequestException
import legends.exceptions.LegendsException
import legends.logic.GameState
import legends.models.GameStage.PILOT
import legends.models.GameStage.REGISTRATION
import legends.models.TeamModel
import legends.models.UserModel
import legends.models.UserRole
import legends.utils.ValidationUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService(
        private val teamDao: TeamDao,
        private val userDao: UserDao
) {

    companion object {
        const val MAX_TEAM_SIZE = 8
        const val MIN_TEAM_SIZE = 3
    }

    @Synchronized
    @Transactional
    fun createTeam(userId: Long, teamSignUp: TeamSignUp): TeamModel {
        when(GameState.stage) {
            REGISTRATION, PILOT -> Unit
            else -> throw BadRequestException { "Создавать команды можно только на этапе регистрации." }
        }

        val userData = userDao.getUserOrThrow(userId)
        if (userData.teamId != null) {
            throw BadRequestException { "Вы уже состоите в команде №${userData.teamId}." }
        }
        if (userData.role != UserRole.PLAYER) {
            throw BadRequestException { "Чтобы создать команду, вы должны быть обычным участником." }
        }
        val reason = ValidationUtils.validateNewTeam(teamSignUp)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        val team = teamDao.createTeam(userId, teamSignUp.teamName.trim())
        userDao.setTeamId(userId, team.teamId)
        userDao.setRole(userId, UserRole.CAPTAIN)

        return team
    }

    @Synchronized
    @Transactional
    fun updateTeamName(userId: Long, teamName: TeamSignUp): TeamModel {
        when(GameState.stage) {
            REGISTRATION, PILOT -> Unit
            else -> throw BadRequestException { "Навзание команды разрешено менять только на этапе регистрации." }
        }

        val teamId = userDao.getUserOrThrow(userId).checkCaptain()

        val reason = ValidationUtils.validateNewTeam(teamName)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        teamDao.updateTeamName(teamId, teamName.teamName.trim())

        return teamDao.getTeamOrThrow(teamId)
    }

    @Synchronized
    @Transactional
    fun joinUserToTeam(userId: Long, join: TeamJoin): TeamModel {

        val user = userDao.getUserOrThrow(userId)
        if (user.teamId != null) {
            throw BadRequestException { "Вы уже состоите в команде №${join.teamId}." }
        }

        val team = teamDao.getTeamOrThrow(join.teamId)
        if (team.size >= MAX_TEAM_SIZE) {
            throw BadRequestException { "Макисмально число участников в команде [$MAX_TEAM_SIZE]" }
        }
        if (!team.inviteCode.equals(join.inviteCode, ignoreCase = true)) {
            throw BadRequestException { "Неверный пригласительный код" }
        }
        if (user.role != UserRole.PLAYER) {
            throw BadRequestException { "Только обычные игроки могут вступать в команды." }
        }

        userDao.setTeamId(userId = userId, teamId = join.teamId)
        return team.copy(size = team.size + 1)
    }

    @Synchronized
    fun kickUser(captainId: Long, kickId: Long) {
        val captain = userDao.getUserOrThrow(captainId)
        val teamId = captain.checkCaptain()
        val kickUser = userDao.getUserOrThrow(kickId)

        if (teamId != kickUser.teamId) {
            throw LegendsException(HttpStatus.FORBIDDEN)
            { "Вы не можете исключить игрока чужой команды." }
        }

        if (captain.userId == kickId) {
            throw LegendsException(HttpStatus.BAD_REQUEST)
            { "Вы не можете исключить себя, так как являетесь капитаном команды." }
        }

        userDao.setTeamId(kickId, null)
    }

    fun getTeamByUserId(userId: Long): TeamModel? {
        return teamDao.getTeamByUser(userId)
    }

    fun getTeammates(userId: Long): List<UserModel>? {
        val user = userDao.getUserOrThrow(userId)
        user.teamId ?: return null
        return userDao.getUsersByTeamId(user.teamId)
    }

    fun getAllTeams(): List<TeamModel> {
        return teamDao.getAllTeams()
    }

    @Synchronized
    @Transactional
    fun changePartyLeader(oldCaptainId: Long, newCaptainId: Long): TeamModel {
        val oldCaptain = userDao.getUserOrThrow(oldCaptainId)
        val newCaptain = userDao.getUserOrThrow(newCaptainId)

        val teamId = oldCaptain.checkCaptain()
        if (newCaptain.teamId != teamId) {
            throw BadRequestException { "Нельзя назначить капитаном члена другой команды." }
        }
        if (newCaptain.role != UserRole.PLAYER) {
            throw BadRequestException { "Новый капитан должен быть обычным игроком." }
        }

        userDao.setRole(oldCaptainId, UserRole.PLAYER)
        userDao.setRole(newCaptainId, UserRole.CAPTAIN)
        teamDao.setTeamLeader(captainId = newCaptainId, teamId = teamId)

        return teamDao.getTeamOrThrow(teamId)
    }

    @Transactional
    fun selfKick(userId: Long) {
        val user = userDao.getUserOrThrow(userId)
        if (user.role == UserRole.CAPTAIN) {
            selfKickCaptain(user.userId)
            return
        }
        userDao.setTeamId(user.userId, null)
        userDao.setRole(user.userId, UserRole.PLAYER)
    }

    fun selfKickCaptain(captainId: Long) {

        if (GameState.stage != REGISTRATION) {
            throw LegendsException(HttpStatus.BAD_REQUEST)
            { "Во время игровых этапов капитан не может покинуть команду." }
        }

        val teamId = userDao.getUserOrThrow(captainId).checkCaptain()
        val team = teamDao.getTeamOrThrow(teamId)

        if (team.size != 1) {
            throw LegendsException(HttpStatus.BAD_REQUEST)
            { "Вы не можете выйти из команды, пока в ней находятся другие участники." }
        }

        userDao.setTeamId(captainId, null)
        userDao.setRole(captainId, UserRole.PLAYER)
        teamDao.deleteTeam(teamId)
    }
}