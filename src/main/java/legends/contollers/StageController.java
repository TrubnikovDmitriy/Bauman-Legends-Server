package legends.contollers;

import legends.Configuration;
import legends.dao.PilotStageDAO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/controlpanel")
public class StageController {

	private final @NotNull PilotStageDAO pilotStageDAO;

	public StageController(@NotNull PilotStageDAO pilotStageDAO) {
		this.pilotStageDAO = pilotStageDAO;
	}

	@GetMapping
	public String simpleGet() {
		return "<h1>Панель упарвления:</h1>" +
				"<br/>" +
				"<h2>Запустить разогревочный этап: GET /controlpanel/start/pilot</h2>" +
				"<h2>Запустить финальный этап: GET /controlpanel/start/final</h2>" +
				"<br/>" +
				"<h3>Разогрев: " + Configuration.pilotStage + "</h3>" +
				"<h3>Финал: " + Configuration.finalStage + "</h3>";

		// TODO return the secret key in prod
	}

//	@GetMapping("/start/pilot/{legendKey}")
//	public String startPilot(@PathVariable String legendKey) {
	@GetMapping("/start/pilot")
	public String startPilot() {
//		final String secretKey = System.getenv("LEGEND_KEY");
//		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		Configuration.pilotStage = true;
		Configuration.finalStage = false;
		return "<h2>Разогревочный этап запущен.</h2><h2>Финальный этап остановлен.</h2>";
	}

//	@GetMapping("/start/final/{legendKey}")
//	public String startFinal(@PathVariable String legendKey) {
	@GetMapping("/start/final")
	public String startFinal() {
//		final String secretKey = System.getenv("LEGEND_KEY");
//		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		pilotStageDAO.stopPilotStage();
		Configuration.pilotStage = false;
		Configuration.finalStage = true;
		return "<h2>Разогревочный этап остановлен.</h2><h2>Финальный этап запущен.</h2>";
	}
}
