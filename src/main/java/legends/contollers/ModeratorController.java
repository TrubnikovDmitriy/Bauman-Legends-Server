package legends.contollers;

import legends.Configuration;
import legends.dao.FinalStageDAO;
import legends.dao.PilotStageDAO;
import legends.dao.TeamDAO;
import legends.exceptions.LegendException;
import legends.views.ErrorView;
import legends.responseviews.PhotoAnswer;
import legends.responseviews.Table;
import legends.responseviews.TeamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(path = "/moderator")
public class ModeratorController {

	private final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

	private final @NotNull TeamDAO teamDAO;
	private final @NotNull PilotStageDAO pilotStageDAO;
	private final @NotNull FinalStageDAO finalStageDAO;

	public ModeratorController(@NotNull TeamDAO teamDAO,
	                           @NotNull PilotStageDAO pilotStageDAO,
	                           @NotNull FinalStageDAO finalStageDAO) {
		this.teamDAO = teamDAO;
		this.pilotStageDAO = pilotStageDAO;
		this.finalStageDAO = finalStageDAO;
	}

	@GetMapping("/team")
	public ResponseEntity<Table> getTeams(
			@RequestParam(name = "full", defaultValue = "false") Boolean flag) {
		return new ResponseEntity<>(
				new Table(teamDAO.getTeams(flag)),
				HttpStatus.OK
		);
	}

	@GetMapping("/team/{teamID}")
	public ResponseEntity<TeamInfo> getTeam(@PathVariable Integer teamID) {
		logger.info("Get login-pass for team #" + teamID);
		final TeamInfo teamInfo = teamDAO.getTeamForModerator(teamID);
		return new ResponseEntity<>(teamInfo, HttpStatus.OK);
	}

	@GetMapping("/photo/{teamID}")
	public ResponseEntity<PhotoAnswer> getPhotoKey(@PathVariable Integer teamID) {
		logger.info("Get photo key for  team #" + teamID);
		final PhotoAnswer photoAnswer = pilotStageDAO.getPhotoKey(teamID);
		return new ResponseEntity<>(photoAnswer, HttpStatus.OK);
	}

	@GetMapping("/prepare/{teamID}")
	public ResponseEntity prepareTeam(@PathVariable Integer teamID) {
		logger.info("Check preparing team #" + teamID);
		return new ResponseEntity<>(
				pilotStageDAO.prepareTeam(teamID),
				HttpStatus.OK
		);
	}

	@GetMapping("/start/{teamID}")
	public ResponseEntity startTeam(@PathVariable Integer teamID) {

		logger.info("Trying to start a team #" + teamID);
		if (!Configuration.finalStage) {
			return new ResponseEntity<>(
					new ErrorView("Финальный этап еще не начинался. " +
							"Если же вы уверены, что сейчас 12 октября, " +
							"срочно напишите Трубникову 'vk.com/trubnikovdv'."),
					HttpStatus.BAD_REQUEST
			);
		}

		finalStageDAO.startTeam(teamID);
		logger.info("Team #" + teamID + " has started");
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@GetMapping("/stop/{teamID}")
	public ResponseEntity stopTeam(@PathVariable Integer teamID) {

		logger.info("Trying to stop a team #" + teamID);
		if (!Configuration.finalStage) {
			return new ResponseEntity<>(
					new ErrorView("Финальный этап еще не начинался. " +
							"Если же вы уверены, что сейчас 12 октября, " +
							"срочно напишите Трубникову 'vk.com/trubnikovdv'."),
					HttpStatus.BAD_REQUEST
			);
		}

		finalStageDAO.stopTeam(teamID);
		logger.info("Team #" + teamID + " has stopped");
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}


	@ExceptionHandler(LegendException.class)
	public ResponseEntity<ErrorView> excpetionHandler(LegendException exception) {
		logger.info("ExceptionHandler", exception);
		return new ResponseEntity<>(
				new ErrorView(exception.getErrorMessage()),
				exception.getStatus()
		);
	}
}
