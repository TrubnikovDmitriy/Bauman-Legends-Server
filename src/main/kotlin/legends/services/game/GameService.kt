package legends.services.game

import legends.dto.AnswerDto
import legends.models.FactModel
import legends.models.TeamState

interface GameService {

    fun getCurrentTask(userId: Long): TeamState

    fun startNextTask(captainId: Long): TeamState

    fun tryAnswer(userId: Long, dto: AnswerDto): Boolean

    fun skipTask(userId: Long)

    fun getFact(userId: Long): FactModel?
}