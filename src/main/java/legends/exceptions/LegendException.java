package legends.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public abstract class LegendException extends RuntimeException {

	@NonNull protected String errorMessage;
	@NonNull protected HttpStatus status;

	public LegendException(Throwable throwable) {
		super(throwable);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
