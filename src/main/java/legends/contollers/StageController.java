package legends.contollers;

import legends.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/controlpanel")
public class StageController {

	@GetMapping
	public String simpleGet() {
		return "<h1>Панель упарвления:</h1>" +
				"<br/>" +
				"<h2>Запустить разогревочный этап: GET /controlpanel/startpilot/{key}</h2>" +
				"<h2>Запустить финальный этап: GET /controlpanel/startfinal/{key}</h2>" +
				"<br/>" +
				"<h3>Отключить разогревочный этап: GET /controlpanel/stoppilot/{key}</h3>" +
				"<h3>Запустить финальный этап: GET /controlpanel/stopfinal/{key}</h3>";
	}

	@GetMapping("/startpilot/{legendKey}")
	public String startPilot(@PathVariable String legendKey) {
		final String secretKey = System.getenv("LEGEND_KEY");
		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		Configuration.pilotStage = true;
		return "<h2>Разогревочный этап запущен.</h2>";
	}

	@GetMapping("/startfinal/{legendKey}")
	public String startFinal(@PathVariable String legendKey) {
		final String secretKey = System.getenv("LEGEND_KEY");
		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		Configuration.finalStage = true;
		return "<h2>Финальный этап запущен.</h2>";
	}

	@GetMapping("/stoppilot/{legendKey}")
	public String stoptPilot(@PathVariable String legendKey) {
		final String secretKey = System.getenv("LEGEND_KEY");
		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		Configuration.pilotStage = false;
		return "<h2>Разогревочный этап остановлен.</h2>";
	}

	@GetMapping("/stopfinal/{legendKey}")
	public String stopFinal(@PathVariable String legendKey) {
		final String secretKey = System.getenv("LEGEND_KEY");
		if (!secretKey.equals(legendKey)) return "<h2>Неверный ключ</h2>";
		Configuration.finalStage = false;
		return "<h2>Финальный этап остановлен.</h2>";
	}
}
