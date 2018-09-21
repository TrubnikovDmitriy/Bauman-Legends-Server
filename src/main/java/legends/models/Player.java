package legends.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

	@JsonProperty(value = "first_name", required = true)
	private String firstName;
	@JsonProperty(value = "second_name", required = true)
	private String secondName;
	@JsonIgnore
	private Integer age;


	public Player() {
		// Nullable constructor is required for Jackson serializer
	}

	public Player(String firstName, String secondName) {
		this.firstName = firstName;
		this.secondName = secondName;
	}


	// Boilerplate code
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
