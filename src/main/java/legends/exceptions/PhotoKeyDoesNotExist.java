package legends.exceptions;

import org.springframework.http.HttpStatus;

public class PhotoKeyDoesNotExist extends LegendException {

	public PhotoKeyDoesNotExist(Throwable throwable, Integer teamID) {
		super(throwable);
		errorMessage =
				"Скорее всего, команды под номером '" + teamID +  "' не существует, " +
				"или она существует, но уже полностью прошла разогревочный этап. " +
				"Если Вы все-таки на 100% уверены, что произошла какая-то ошибка, " +
				"то напишите Трубникову Диме 'vk.com/trubnikovdv'.";
		status = HttpStatus.NOT_FOUND;
	}
}
