package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.Router;

import java.util.List;

public class Paths {

	@JsonProperty("routers")
	private List<Router> routers;

	public Paths(List<Router> routers) {
		this.routers = routers;
	}
}
