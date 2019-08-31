package legends.exceptions

import org.springframework.http.HttpStatus

open class LegendsException(
        throwable: Throwable? = null,
        val status: HttpStatus,
        val errorMessage: () -> String
) : RuntimeException(throwable) {
    constructor(
            status: HttpStatus,
            errorMessage: () -> String
    ) : this(null, status, errorMessage)
}
