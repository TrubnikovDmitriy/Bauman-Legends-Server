package legends.exceptions;

import org.springframework.http.HttpStatus;

public class CriticalInternalError extends LegendException {

	public CriticalInternalError(final String message) {
		this.status = HttpStatus.INTERNAL_SERVER_ERROR;
		this.errorMessage = message;
	}
}
