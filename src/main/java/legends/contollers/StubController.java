package legends.contollers;

import legends.models.TeamType;
import legends.requestviews.Authentication;
import legends.responseviews.TeamAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
public class StubController {

	@GetMapping(path = "/get")
	public String simpleGet() {
		return "Hello, Vlad!";
	}

	@PostMapping
	public ResponseEntity<TeamAuth> simplePost(@RequestBody Authentication body) {
		if (body.getLogin().equals("admin")) return new ResponseEntity<>(
				new TeamAuth(body.getLogin(), 1, TeamType.ADMIN), HttpStatus.OK);
		if (body.getLogin().equals("moderator")) return new ResponseEntity<>(
				new TeamAuth(body.getLogin(), 2, TeamType.MODERATOR), HttpStatus.OK);
		if (body.getLogin().equals("player")) return new ResponseEntity<>(
				new TeamAuth(body.getLogin(), 42, TeamType.PLAYER), HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}
