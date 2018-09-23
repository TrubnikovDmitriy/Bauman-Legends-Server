package legends.exceptions;

import org.springframework.http.HttpStatus;

public class WrongTaskID extends LegendException {

	public WrongTaskID() {
		this.status = HttpStatus.BAD_REQUEST;
		this.errorMessage = "У вашей команды сейчас другое задание, попробуйте обновить страницу";
	}
}
