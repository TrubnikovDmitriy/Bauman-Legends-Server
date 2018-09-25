package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.Configuration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Trail {

	@JsonProperty("task_id")
	private Integer taskID;

	@JsonProperty("success")
	private Boolean success;

	@JsonProperty("start_time")
	private Integer startTime;

	@JsonProperty("finish_time")
	private Integer finishTime;


	public static final class Mapper implements RowMapper<Trail> {
		@Override
		public Trail mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Trail trail = new Trail();
			trail.taskID = rs.getInt("task_id");
			trail.success = rs.getBoolean("success");
			trail.startTime = rs.getInt("start_time");
			trail.finishTime = rs.getInt("finish_time");
			return trail;
		}
	}
}
