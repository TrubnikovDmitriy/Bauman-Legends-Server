package legends.contollers;

import legends.dao.TeamDAO;
import legends.responseviews.Table;
import legends.responseviews.TeamInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(path = "/player")
public class PlayerController {

	private final @NotNull TeamDAO teamDAO;

	public PlayerController(@NotNull TeamDAO teamDAO) {
		this.teamDAO = teamDAO;
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
	public ResponseEntity getTeams(@PathVariable Integer teamID) {
//		try {
			final TeamInfo teamInfo = teamDAO.getTeamForPlayer(teamID);
//		} catch ()
		return new ResponseEntity<>(teamInfo, HttpStatus.OK);
	}
}
