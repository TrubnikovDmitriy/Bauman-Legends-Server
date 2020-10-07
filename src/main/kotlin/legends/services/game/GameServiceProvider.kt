package legends.services.game

import legends.dao.GameDao
import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dto.AnswerDto
import legends.logic.GameState
import legends.logic.QuestTimer
import legends.models.FactModel
import legends.models.GameStage.*
import legends.models.TeamState
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GameServiceProvider(
        private val gameDao: GameDao,
        private val userDao: UserDao,
        private val teamDao: TeamDao,
        private val questTimer: QuestTimer
) : GameService {

    private val logger = LoggerFactory.getLogger(GameService::class.java)

    private val pilot: GameService by lazy { GameServicePilot(gameDao, userDao, teamDao) }
    private val final: GameService by lazy { GameServiceFinal(gameDao, userDao, teamDao, questTimer) }


    override fun getCurrentTask(userId: Long): TeamState {
        return when (GameState.stage) {
            PILOT -> pilot.getCurrentTask(userId)
            FINAL -> final.getCurrentTask(userId)
            REGISTRATION -> TeamState.stop("Легенды Бауманки начнутся 5 октября. Осталось совсем чуть-чуть!")
            FINISH -> TeamState.stop("Поздравляем! Вы прошли все испытания, Легенды Бауманки 2020 завершены!")
        }
    }

    override fun startNextTask(captainId: Long): TeamState {
        return when (GameState.stage) {
            PILOT -> pilot.startNextTask(captainId)
            FINAL -> final.startNextTask(captainId)
            REGISTRATION -> TeamState.stop("Первое задание можно будет получить 5 октября.")
            FINISH -> TeamState.stop("Легенды Бауманки завершены.")
        }
    }

    override fun tryAnswer(userId: Long, dto: AnswerDto): Boolean {
        return when (GameState.stage) {
            PILOT -> pilot.tryAnswer(userId, dto)
            FINAL -> final.tryAnswer(userId, dto)
            else -> {
                logger.warn("Try to answer the task when game stage is [${GameState.stage}]")
                false
            }
        }
    }

    override fun skipTask(userId: Long) {
        when (GameState.stage) {
            PILOT -> pilot.skipTask(userId)
            FINAL -> final.skipTask(userId)
            else -> logger.warn("Try to skip task when game stage is [${GameState.stage}]")
        }
    }

    override fun getFact(userId: Long): FactModel? {
        return when (GameState.stage) {
            PILOT -> pilot.getFact(userId)
            FINAL -> final.getFact(userId)
            else -> null
        }
    }
}