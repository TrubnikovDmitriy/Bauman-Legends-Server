package legends.contollers;

import legends.Configuration;
import legends.dao.AuthDAO;
import legends.dao.FinalStageDAO;
import legends.dao.PilotStageDAO;
import legends.dao.TeamDAO;
import legends.exceptions.LegendException;
import legends.requestviews.FullTeam;
import legends.responseviews.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(path = "/moderator")
public class ModeratorController {

	private final @NotNull AuthDAO authDAO;
	private final @NotNull TeamDAO teamDAO;
	private final @NotNull PilotStageDAO pilotStageDAO;
	private final @NotNull FinalStageDAO finalStageDAO;

	public ModeratorController(@NotNull AuthDAO authDAO,
	                           @NotNull TeamDAO teamDAO,
	                           @NotNull PilotStageDAO pilotStageDAO,
	                           @NotNull FinalStageDAO finalStageDAO) {
		this.authDAO = authDAO;
		this.teamDAO = teamDAO;
		this.pilotStageDAO = pilotStageDAO;
		this.finalStageDAO = finalStageDAO;
	}

	@PostMapping("/team")
	public ResponseEntity signUpTeam(@RequestBody FullTeam team) {
		if (!team.isValid()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		authDAO.signUpTeam(team);
		return new ResponseEntity(HttpStatus.CREATED);
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
		final TeamInfo teamInfo = teamDAO.getTeamForModerator(teamID);
		return new ResponseEntity<>(teamInfo, HttpStatus.OK);
	}

	@GetMapping("/photo/{teamID}")
	public ResponseEntity<PhotoAnswer> getPhotoKey(@PathVariable Integer teamID) {
		final PhotoAnswer photoAnswer = pilotStageDAO.getPhotoKey(teamID);
		return new ResponseEntity<>(photoAnswer, HttpStatus.OK);
	}

	@GetMapping("/prepare/{teamID}")
	public ResponseEntity prepareTeam(@PathVariable Integer teamID) {
		return new ResponseEntity<>(
				pilotStageDAO.prepareTeam(teamID),
				HttpStatus.OK
		);
	}

	@GetMapping("/start/{teamID}")
	public ResponseEntity startTeam(@PathVariable Integer teamID) {

		if (!Configuration.finalStage) {
			return new ResponseEntity<>(
					new ErrorMessage("Финальный этап еще не начинался. " +
							"Если же вы уверены, что сейчас 12 октября, " +
							"срочно напишите Трубникову 'vk.com/trubnikovdv'."),
					HttpStatus.BAD_REQUEST
			);
		}
		finalStageDAO.startTeam(teamID);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@GetMapping("/stop/{teamID}")
	public ResponseEntity stopTeam(@PathVariable Integer teamID) {

		if (!Configuration.finalStage) {
			return new ResponseEntity<>(
					new ErrorMessage("Финальный этап еще не начинался. " +
							"Если же вы уверены, что сейчас 12 октября, " +
							"срочно напишите Трубникову 'vk.com/trubnikovdv'."),
					HttpStatus.BAD_REQUEST
			);
		}
		finalStageDAO.stopTeam(teamID);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}


	@ExceptionHandler(LegendException.class)
	public ResponseEntity<ErrorMessage> excpetionHandler(LegendException exception) {
		return new ResponseEntity<>(
				new ErrorMessage(exception.getErrorMessage()),
				exception.getStatus()
		);
	}
}
