package legends.requestviews;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

	@JsonProperty private String firstName;
	@JsonProperty private String secondName;
	@JsonProperty private String phone;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
