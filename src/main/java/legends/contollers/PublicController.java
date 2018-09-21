package legends.contollers;

import legends.businesslogic.SomeUsefulWork;
import legends.dao.AuthDAO;
import legends.models.TeamType;
import legends.requestviews.Authentication;
import legends.responseviews.TeamInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
public class PublicController {

	private final SomeUsefulWork usefulWork;
	private final AuthDAO authDAO;

	public PublicController(SomeUsefulWork usefulWork, AuthDAO authDAO) {
		this.usefulWork = usefulWork;
		this.authDAO = authDAO;
	}

	@GetMapping(path = "/get")
	public String simpleGet() {
		return "Hello, Vlad!";
	}

	@PostMapping
	public ResponseEntity<TeamInfo> simplePost(@RequestBody Authentication body) {
		if (body.getLogin().equals("admin")) return new ResponseEntity<>(
				new TeamInfo(body.getLogin(), 1, TeamType.ADMIN), HttpStatus.OK);
		if (body.getLogin().equals("moderator")) return new ResponseEntity<>(
				new TeamInfo(body.getLogin(), 2, TeamType.MODERATOR), HttpStatus.OK);
		if (body.getLogin().equals("player")) return new ResponseEntity<>(
				new TeamInfo(body.getLogin(), 42, TeamType.PLAYER), HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}


	@GetMapping(path = "/auth/{pathVariable}")
	public ResponseEntity<TeamInfo> getWithPathVariables(@PathVariable String pathVariable) {
		return new ResponseEntity<>(
				authDAO.getUser("admin", pathVariable),
				HttpStatus.ACCEPTED
		);
	}


	@GetMapping(path = "/get/params")
	public String getWithQueryParams(
			@RequestParam(name = "foo", required = false, defaultValue = "42") Integer integer,
			@RequestParam(name = "bar", required = true) Boolean flag
	) {
		return integer + "-" + flag + ": " + usefulWork.doWork();
	}


//	@PostMapping(path = "/post/{teamName}")
//	public ResponseEntity<TeamInfo> getWithQueryParams(
//			@RequestBody Player player,
//			@PathVariable String teamName
//	) {
//		return new ResponseEntity<>(
//				new TeamInfo(teamName, player),
//				HttpStatus.CREATED
//		);
//	}
}
