package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TeamIsFinished extends LegendException {

	public TeamIsFinished() {
		errorMessage = "Поздравляем! Вы познали все Легенды Бауманки!";
		status = HttpStatus.I_AM_A_TEAPOT;
	}
}
