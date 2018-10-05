package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhotoAnswer {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("answer")
	private String answer;

	@JsonProperty("key")
	private String key;

	public static final class Mapper implements RowMapper<PhotoAnswer> {

		@Override
		public PhotoAnswer mapRow(ResultSet rs, int rowNum) throws SQLException {
			final PhotoAnswer answer = new PhotoAnswer();

			answer.id = rs.getInt("id");
			answer.answer = rs.getString("answer");
			answer.key = rs.getString("key");

			return answer;
		}
	}
}
