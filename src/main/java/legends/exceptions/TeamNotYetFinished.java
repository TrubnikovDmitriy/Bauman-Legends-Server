package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TeamNotYetFinished extends LegendException {

	public TeamNotYetFinished(final Integer teamID) {
		this.status = HttpStatus.BAD_REQUEST;
		this.errorMessage = "Команда №" + teamID + " еще не готова финишировать. " +
				"Если они вдруг уже оказались на крыше, " +
				"то напишите Диме 'https://vk.com/trubnikovdv'";
	}
}
