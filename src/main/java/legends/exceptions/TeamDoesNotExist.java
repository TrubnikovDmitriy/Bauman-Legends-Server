package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TeamDoesNotExist extends LegendException {

	public TeamDoesNotExist(Throwable throwable, Integer teamID) {
		super(throwable);
		errorMessage = "Команды под номером " + teamID + " не существует";
		status = HttpStatus.NOT_FOUND;
	}
}
