package legends.contollers;

import legends.dao.AuthDAO;
import legends.dao.TeamDAO;
import legends.requestviews.FullTeam;
import legends.responseviews.TeamTable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequestMapping(path = "/moderator")
public class ModeratorController {

	private final @NotNull AuthDAO authDAO;
	private final @NotNull TeamDAO teamDAO;

	public ModeratorController(@NotNull AuthDAO authDAO,
	                           @NotNull TeamDAO teamDAO) {
		this.authDAO = authDAO;
		this.teamDAO = teamDAO;
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
	public ResponseEntity<List<TeamTable>> getTeams(
			@RequestParam(name = "full", defaultValue = "false") Boolean flag) {
		return new ResponseEntity<>(teamDAO.getTeams(flag), HttpStatus.OK);
	}
}
