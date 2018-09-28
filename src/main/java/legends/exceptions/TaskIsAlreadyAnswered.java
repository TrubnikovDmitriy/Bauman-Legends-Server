package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TaskIsAlreadyAnswered extends LegendException {

	public TaskIsAlreadyAnswered() {
		this.status = HttpStatus.FORBIDDEN;
		this.errorMessage = "Вы уже отвечали на этот вопрос";
	}
}
