package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class KeyAnswer {

	@JsonProperty("is_accepted")
	private Boolean accept;

	@JsonProperty("points")
	private Integer points;

	@JsonProperty("statue_number")
	private Integer numberOfStatue;


	public void setAccept(Boolean accept) {
		this.accept = accept;
	}

	@JsonIgnore
	public Boolean isAccepted() {
		return accept;
	}

	@JsonIgnore
	public Integer getPoints() {
		return points;
	}

	public static final class Mapper implements RowMapper<KeyAnswer> {

		@Override
		public KeyAnswer mapRow(ResultSet rs, int rowNum) throws SQLException {
			final KeyAnswer accept = new KeyAnswer();

			accept.points = rs.getInt("points");
			accept.numberOfStatue = rs.getInt("statue_number");
			if (rs.wasNull()) accept.numberOfStatue = null;

			return accept;
		}
	}
}
