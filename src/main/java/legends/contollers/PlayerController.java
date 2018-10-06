package legends.contollers;

import legends.Configuration;
import legends.dao.FinalStageDAO;
import legends.dao.PilotStageDAO;
import legends.dao.TeamDAO;
import legends.exceptions.LegendException;
import legends.models.TaskType;
import legends.requestviews.Answer;
import legends.requestviews.SecretKey;
import legends.responseviews.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping(path = "/player")
public class PlayerController {

	private final Logger logger = LoggerFactory.getLogger(PlayerController.class);

	private final @NotNull FinalStageDAO finalStageDAO;
	private final @NotNull PilotStageDAO pilotStageDAO;
	private final @NotNull TeamDAO teamDAO;

	public PlayerController(@NotNull FinalStageDAO finalStageDAO,
	                        @NotNull PilotStageDAO pilotStageDAO,
	                        @NotNull TeamDAO teamDAO) {
		this.finalStageDAO = finalStageDAO;
		this.pilotStageDAO = pilotStageDAO;
		this.teamDAO = teamDAO;
	}

	@GetMapping("/team")
	public ResponseEntity<Table> getTeams(@RequestParam(name = "full", defaultValue = "false") Boolean flag) {
		return new ResponseEntity<>(
				new Table(teamDAO.getTeams(flag)),
				HttpStatus.OK
		);
	}

	@GetMapping("/team/{teamID}")
	public ResponseEntity getTeams(@PathVariable Integer teamID) {
		final TeamInfo teamInfo = teamDAO.getTeamForPlayer(teamID);
		return new ResponseEntity<>(teamInfo, HttpStatus.OK);
	}

	@GetMapping("/task/{teamID}")
	public ResponseEntity getCurrentTask(@PathVariable Integer teamID) {

		if (Configuration.finalStage) return getCurrentTaskFinal(teamID);
		if (Configuration.pilotStage) return getCurrentTaskPilot(teamID);

		// Если команда получила логин-пароль до того, как я запустил разогрев.
		return new ResponseEntity<>(
				new ErrorMessage(
						"Легенды Бауманки начнутся уже совсем скоро!\n" +
						"Уже в понедельник 8 октября Вы сможете получить первое задание!"
				),
				HttpStatus.BAD_REQUEST
		);
	}
	private ResponseEntity getCurrentTaskFinal(final Integer teamID) {

		final FinalTask currentTask = finalStageDAO.getCurrentTask(teamID);

		if (currentTask == null) {
			return new ResponseEntity<>(
					new ErrorMessage(
							"Ваша команада еще не начала прохождение финального этапа. " +
							"Чтобы приступить к первому заданию, приходите в назначенное Вам время к стойке регистрации. " +
							"Регистрация будет проходить в пятницу 12 октября в Главном Здании на 1ом этаже возле входа со стороны Яузы."
					),
					HttpStatus.BAD_REQUEST
			);
		}

		return new ResponseEntity<>(currentTask, HttpStatus.OK);
	}
	private ResponseEntity getCurrentTaskPilot(final Integer teamID) {
		return new ResponseEntity<>(
				pilotStageDAO.getCurrentTask(teamID),
				HttpStatus.OK
		);
	}

	@PostMapping("/task")
	public ResponseEntity checkAnswer(@RequestBody Answer answer) {

		teamDAO.validateAnswer(answer);
		if (Configuration.finalStage) return checkAnswerFinal(answer);
		if (Configuration.pilotStage) return checkAnswerPilot(answer);

		return new ResponseEntity<>(
				new ErrorMessage("Ни один из этапов Легенд еще не запущен"),
				HttpStatus.BAD_REQUEST
		);
	}
	private ResponseEntity checkAnswerFinal(final Answer answer) {
		if (answer.getTaskType() != TaskType.FINAL) {
			return new ResponseEntity<>(
					new ErrorMessage("Разогревочный этап уже завершён."),
					HttpStatus.BAD_REQUEST
			);
		}

		final boolean isCorrect = finalStageDAO.checkAnswer(
				answer.getTaskID(),
				answer.getAnswer()
		);

		if (isCorrect) {
			// Stop the timer and increase score
			finalStageDAO.acceptCurrentTask(answer.getTeamID(), answer.getTaskID());
		}

		return new ResponseEntity<>(
				new Result(isCorrect, TaskType.FINAL, null),
				HttpStatus.ACCEPTED
		);
	}
	private ResponseEntity checkAnswerPilot(final Answer answer) {
		if (answer.getTaskType() == TaskType.FINAL) {
			return new ResponseEntity<>(
					new ErrorMessage("Финальный этап начнёт в пятницу 12 октября."),
					HttpStatus.BAD_REQUEST
			);
		}

		final Result result = pilotStageDAO.checkAnswer(
				answer.getTaskID(),
				answer.getAnswer(),
				answer.getTaskType()
		);

		if (result.isCorrect()) {
			pilotStageDAO.acceptCurrentAndTakeNextTasks(
					answer.getTeamID(),
					answer.getTaskID(),
					true
			);
		}
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	@PostMapping("/next")
	public ResponseEntity takeNextAnswer(@RequestBody Answer answer) {

		if (Configuration.finalStage) return takeNextAnswerFinal(answer);
		if (Configuration.pilotStage) return takeNextAnswerExtra(answer);

		return new ResponseEntity<>(
				new ErrorMessage("Ни один из этапов Легенд еще не запущен"),
				HttpStatus.BAD_REQUEST
		);
	}
	private ResponseEntity takeNextAnswerFinal(final Answer answer) {

		final boolean isAnswered = finalStageDAO.isAnswered(
				answer.getTeamID(),
				answer.getTaskID()
		);

		if (!isAnswered) {
			return new ResponseEntity<>(
					new ErrorMessage("Сначала ответьте на текущий вопрос"),
					HttpStatus.FORBIDDEN
			);
		}

		finalStageDAO.takeNextTask(answer.getTeamID(), answer.getTaskID());
		return getCurrentTaskFinal(answer.getTeamID());
	}
	private ResponseEntity takeNextAnswerExtra(final Answer answer) {

		if (answer.getTaskType() != TaskType.EXTRA) {
			return new ResponseEntity<>(
					new ErrorMessage("Пропускать можно только дополнительные задания"),
					HttpStatus.FORBIDDEN
			);
		}

		pilotStageDAO.acceptCurrentAndTakeNextTasks(
				answer.getTeamID(),
				answer.getTaskID(),
				false
		);
		return getCurrentTaskPilot(answer.getTeamID());
	}

	@GetMapping("/statues/{teamID}")
	public ResponseEntity<OpenedStatues> getOpenedStatues(@PathVariable Integer teamID) {
		final List<Integer> numbers = teamDAO.getOpenedStatues(teamID);
		return new ResponseEntity<>(new OpenedStatues(numbers), HttpStatus.OK);
	}

	@PostMapping("/statues")
	public ResponseEntity<KeyAnswer> getOpenedStatues(@RequestBody SecretKey secretKey) {
		final KeyAnswer answer = teamDAO.checkKey(secretKey);
		final HttpStatus httpStatus = answer.isAccepted() ? HttpStatus.ACCEPTED : HttpStatus.FORBIDDEN;
		return new ResponseEntity<>(answer, httpStatus);
	}


	@ExceptionHandler(LegendException.class)
	public ResponseEntity<ErrorMessage> excpetionHandler(LegendException exception) {
		logger.debug("Handle exception", exception);
		return new ResponseEntity<>(
				new ErrorMessage(exception.getErrorMessage()),
				exception.getStatus()
		);
	}
}
