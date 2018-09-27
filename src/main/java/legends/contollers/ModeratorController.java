package legends.contollers;

import legends.Configuration;
import legends.dao.AuthDAO;
import legends.dao.PilotStageDAO;
import legends.dao.TeamDAO;
import legends.exceptions.LegendException;
import legends.requestviews.FullTeam;
import legends.responseviews.ErrorMessage;
import legends.responseviews.PhotoKey;
import legends.responseviews.Table;
import legends.responseviews.TeamInfo;
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

	public ModeratorController(@NotNull AuthDAO authDAO,
	                           @NotNull TeamDAO teamDAO,
	                           @NotNull PilotStageDAO pilotStageDAO) {
		this.authDAO = authDAO;
		this.teamDAO = teamDAO;
		this.pilotStageDAO = pilotStageDAO;
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
	public ResponseEntity<PhotoKey> getPhotoKey(@PathVariable Integer teamID) {
		final String key = pilotStageDAO.getPhotoKey(teamID);
		return new ResponseEntity<>(new PhotoKey(key), HttpStatus.OK);
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
		pilotStageDAO.startTeam(teamID);
		return new ResponseEntity(HttpStatus.CREATED);
	}


	@ExceptionHandler(LegendException.class)
	public ResponseEntity<ErrorMessage> excpetionHandler(LegendException exception) {
		return new ResponseEntity<>(
				new ErrorMessage(exception.getErrorMessage()),
				exception.getStatus()
		);
	}
}
