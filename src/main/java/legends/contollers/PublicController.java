package legends.contollers;

import legends.businesslogic.SomeUsefulWork;
import legends.models.Player;
import legends.models.Team;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/public")
public class PublicController {

	private final SomeUsefulWork usefulWork;

	public PublicController(SomeUsefulWork usefulWork) {
		this.usefulWork = usefulWork;
	}


	@GetMapping(path = "/get")
	public String simpleGet() {
		return "Hello, Spring!";
	}


	@GetMapping(path = "/get/{pathVariable}")
	public ResponseEntity<String> getWithPathVariables(@PathVariable String pathVariable) {
		return new ResponseEntity<>(
				pathVariable,
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


	@PostMapping(path = "/post/{teamName}")
	public ResponseEntity<Team> getWithQueryParams(
			@RequestBody Player player,
			@PathVariable String teamName
	) {
		return new ResponseEntity<>(
				new Team(teamName, player),
				HttpStatus.CREATED
		);
	}
}
