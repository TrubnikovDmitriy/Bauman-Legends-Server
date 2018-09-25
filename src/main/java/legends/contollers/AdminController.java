package legends.contollers;

import legends.dao.TeamDAO;
import legends.responseviews.Paths;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {

	private final @NotNull TeamDAO teamDAO;

	public AdminController(@NotNull TeamDAO teamDAO) {
		this.teamDAO = teamDAO;
	}

	@GetMapping("/router")
	public ResponseEntity<Paths> getTeams(@RequestParam(name = "full", defaultValue = "false") Boolean flag) {
		return new ResponseEntity<>(
				new Paths(teamDAO.getRouters(flag)),
				HttpStatus.OK
		);
	}
}
