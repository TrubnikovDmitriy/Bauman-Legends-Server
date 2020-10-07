package legends.services

import legends.dao.TeamDao
import legends.dao.UserDao
import legends.dao.WitnessDao
import legends.dto.WitnessKeywordDto
import legends.exceptions.BadRequestException
import legends.exceptions.TeamIsNotPresented
import legends.logic.GameState
import legends.models.WitnessModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WitnessService(
        private val witnessDao: WitnessDao,
        private val teamDao: TeamDao,
        private val userDao: UserDao
) {

    companion object {
        const val MAIN_WITNESS_ID = 7L
        const val PRE_WITNESS_COUNT = 6
        const val ALL_WITNESS_COUNT = 7
    }

    fun getDecision(userId: Long): String {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        val freedom: Boolean = witnessDao.getDecision(teamId) ?: throw BadRequestException {
            "Ваша команда ещё не приняла решение по Учёному."
        }
        return getTextForDecision(freedom)
    }

    fun setDecision(userId: Long, freedom: Boolean): String {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        witnessDao.setDecision(teamId, freedom)
        return getTextForDecision(freedom)
    }

    fun getWitnesses(userId: Long): List<WitnessModel> {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        return witnessDao.getWitnessesForTeam(teamId)
    }

    @Transactional
    fun openWitness(userId: Long, witnessKeyword: WitnessKeywordDto): WitnessModel {
        val teamId = userDao.getUserOrThrow(userId).teamId ?: throw TeamIsNotPresented()
        val ghost = witnessDao.getWitnessByKeyword(witnessKeyword.keyword) ?: throw BadRequestException {
            "Не удалось найти такого свидетеля."
        }
        if (ghost.ghostId == MAIN_WITNESS_ID) {
            checkFirstSixGhosts(teamId)
        }
        witnessDao.inspectWitness(teamId, ghost.ghostId)
        teamDao.increaseScore(teamId, GameState.SCORE_PER_GHOST)
        return ghost
    }


    private fun checkFirstSixGhosts(teamId: Long) {
        val size = witnessDao.getWitnessesForTeam(teamId).size
        if (size != PRE_WITNESS_COUNT) {
            throw BadRequestException {
                "У вас недостаточно улик, чтобы принять решение по Учёному. " +
                        "Опросите всех свидетелей."
            }
        }
    }

    private fun getTextForDecision(freedom: Boolean): String {
        return if (freedom) {
            "Полностью оправданный неизвестными экспертами, наш ученый был выпущен на свободу. " +
                    "И, похоже, это была большая ошибка. Санитары психбольницы не смогли его поймать. " +
                    "Обиженный на несправедливость мира, он модифицировал свою сыворотку. " +
                    "Теперь для достижения эффекта было достаточно вдохнуть её испарения. " +
                    "Безумец начал мстить, разбивая баночки с веществом в самые неподходящие " +
                    "моменты рядом с теми, кто наговаривал на него, из-за кого он попал в стены лечебницы, " +
                    "и теперь больше не может быть принят в круг многоуважаемых ученых. " +
                    "И никто точно не может сказать, как он поведет себя дальше…\n" +
                    "Сейчас кто-то говорит, что он просто псих, другие — что безумный гений. " +
                    "Каким видите его вы — решать вам, но помните, всего одно слово может решить судьбу человека!"
        } else {
            "Многообещающий ученый всю оставшуюся жизнь провел в психбольнице. " +
                    "В полном одиночестве, всеми забытый, никому не нужный исследователь, " +
                    "который больше не может заниматься любимым делом.\n" +
                    "Благодаря тому, что главный конкурент ученого из Франции “сошел с дистанции”, " +
                    "иностранная разработка продвинулась на первое место и стала очень популярной, " +
                    "используемой. А ведь “сыворотка правды”, придуманная нашим несчастным героем, " +
                    "могла бы принести столько пользы …"
        }
    }
}