package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
			if (rs.wasNull()) trail.success = null;
			trail.startTime = rs.getInt("start_time");
			if (rs.wasNull()) trail.startTime = null;
			trail.finishTime = rs.getInt("finish_time");
			if (rs.wasNull()) trail.finishTime = null;
			return trail;
		}
	}
}
