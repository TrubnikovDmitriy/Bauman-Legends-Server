package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenedStatues {

	@JsonProperty private final List<Integer> numbers;

	public OpenedStatues(List<Integer> numbers) {
		this.numbers = numbers;
	}
}
