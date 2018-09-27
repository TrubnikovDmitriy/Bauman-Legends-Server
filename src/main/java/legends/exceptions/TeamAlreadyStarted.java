package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TeamAlreadyStarted extends LegendException {

	public TeamAlreadyStarted(final Integer teamID) {
		this.status = HttpStatus.BAD_REQUEST;
		this.errorMessage = "Команда №" + teamID + " уже стартанула. " +
				"Eсли вы на 100% уверены, что у них еще не появилось задание, " +
				"то напишите Трубникову 'https://vk.com/trubnikovdv'";
	}
}
