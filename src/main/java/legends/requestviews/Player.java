package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

	@JsonProperty(value = "first_name", required = true) private String firstName;
	@JsonProperty(value = "second_name", required = true) private String secondName;

	public Player() { }

	public boolean isValid() {
		return firstName != null && secondName != null;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
}
