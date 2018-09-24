package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoKey {

	@JsonProperty private final String key;

	public PhotoKey(String key) {
		this.key = key;
	}
}
