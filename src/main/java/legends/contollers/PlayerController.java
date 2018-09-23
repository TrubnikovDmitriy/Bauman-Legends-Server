package legends.contollers;

import legends.Configuration;
import legends.dao.FinalStageDAO;
import legends.dao.PilotStageDAO;
import legends.dao.TeamDAO;
import legends.exceptions.LegendException;
import legends.requestviews.Answer;
import legends.requestviews.Team;
import legends.responseviews.ErrorMessage;
import legends.responseviews.Table;
import legends.responseviews.TeamInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(path = "/player")
public class PlayerController {

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

	@GetMapping("/task")
	public ResponseEntity getCurrentTask(@RequestBody Team team) {

		if (Configuration.finalStage) {
			return new ResponseEntity<>(
					finalStageDAO.getCurrentTask(team.ID),
					HttpStatus.OK
			);

		} else if (Configuration.pilotStage) {
			return new ResponseEntity<>(
					pilotStageDAO.getCurrentTask(team.ID),
					HttpStatus.OK
			);

		} else {
			// TODO: Если команда получила логин-пароль до того, как я запустил разогрев.
			return null;
		}
	}

	@PostMapping("/task")
	public ResponseEntity answerToPilotStage(@RequestBody Answer answer) {
		return null;
	}


	@ExceptionHandler(LegendException.class)
	public ResponseEntity<ErrorMessage> excpetionHandler(LegendException exception) {
		return new ResponseEntity<>(
				new ErrorMessage(exception.getErrorMessage()),
				exception.getStatus()
		);
	}
}
