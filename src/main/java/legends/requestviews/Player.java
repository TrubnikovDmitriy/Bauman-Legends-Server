package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

	@JsonProperty("first_name") private String firstName;
	@JsonProperty("second_name") private String secondName;

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
