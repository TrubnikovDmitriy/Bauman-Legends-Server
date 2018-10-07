package legends.contollers;

import legends.dao.AuthDAO;
import legends.requestviews.Authentication;
import legends.responseviews.TeamAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final @NotNull AuthDAO authDAO;

	public AuthController(@NotNull AuthDAO authDAO) {
		this.authDAO = authDAO;
	}

	@PostMapping
	public ResponseEntity<TeamAuth> signIn(@RequestBody Authentication body) {

		logger.info("Authentication: " + body);
		if (!body.isValid()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		final TeamAuth team = authDAO.getUser(body.getLogin(), body.getPassword());

		if (team != null) {
			logger.info("Authentication success: " + team);
			return new ResponseEntity<>(team, HttpStatus.OK);

		} else {
			logger.info("Authentication failed");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}
