package legends.exceptions;

import org.springframework.http.HttpStatus;

public class PhotoKeyDoesNotExist extends LegendException {

	public PhotoKeyDoesNotExist(Throwable throwable, Integer teamID) {
		super(throwable);
		errorMessage = "На данный момент, команда №" + teamID +  " не выполняет фотоквест.";
		status = HttpStatus.NOT_FOUND;
	}
}
