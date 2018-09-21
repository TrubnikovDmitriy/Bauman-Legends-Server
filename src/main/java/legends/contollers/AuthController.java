package legends.contollers;

import legends.dao.AuthDAO;
import legends.requestviews.Authentication;
import legends.requestviews.FullTeam;
import legends.responseviews.TeamInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

	private final @NotNull AuthDAO authDAO;

	public AuthController(AuthDAO authDAO) {
		this.authDAO = authDAO;
	}

	@PostMapping
	public ResponseEntity<TeamInfo> signIn(@RequestBody Authentication body) {
		if (!body.isValid()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final TeamInfo team = authDAO.getUser(body.getLogin(), body.getPassword());
		if (team != null) {
			return new ResponseEntity<>(team, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("/team")
	public ResponseEntity signUpTeam(@RequestBody FullTeam team) {
		if (!team.isValid()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		authDAO.signUpTeam(team);
		return new ResponseEntity(HttpStatus.CREATED);
	}
}
