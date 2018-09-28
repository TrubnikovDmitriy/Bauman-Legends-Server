package legends.exceptions;

import org.springframework.http.HttpStatus;

public class TaskIsNotExist extends LegendException {

	public TaskIsNotExist() {
		this.status = HttpStatus.BAD_REQUEST;
		this.errorMessage = "Такого задания для указанной команды не существует";
	}
}
