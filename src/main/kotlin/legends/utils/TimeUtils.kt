package legends.utils

import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    fun currentTime(timeUnit: TimeUnit): Long {
        val millis = Calendar.getInstance().timeInMillis
        return timeUnit.convert(millis, TimeUnit.MILLISECONDS)
    }
}