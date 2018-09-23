package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PilotTask {

	@JsonProperty("task_id")
	private Integer id;

	@JsonProperty("task_type")
	private TaskType type;

	@JsonProperty("start_time")
	private Integer startTime;

	@JsonProperty("points")
	private Integer points;


	public static final class Mapper implements RowMapper<PilotTask> {

		@Override
		public PilotTask mapRow(ResultSet rs, int rowNum) throws SQLException {
			final PilotTask task = new PilotTask();

			task.id = rs.getInt("task_id");
			task.startTime = rs.getInt("start_time");
			task.points = rs.getInt("points");
			task.type = TaskType.valueOf(rs.getString("type"));

			return task;
		}
	}
}
