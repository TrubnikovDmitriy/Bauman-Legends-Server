package legends.utils

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * It is used to save answers for tasks in url-encoded format.
 * It helps to avoid bugs with answers with comma, since comma
 * is used to separate multiple equivalent answers.
 *
 * For example: single answer "one, two, three" will be shown
 * as 3 different correct answers: "one", "two", "three".
 *
 * Therefore, the answers need to be encoded before writing
 * and decoded after reading.
 */
object AnswersCoder {

    fun String.encodeAnswer(): String {
        return URLEncoder.encode(this, Charsets.UTF_16)
    }

    fun String.decodeAnswer(): String {
        return URLDecoder.decode(this, Charsets.UTF_16)
    }
}