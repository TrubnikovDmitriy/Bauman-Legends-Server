package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

	@JsonProperty("points")
	private Integer points;

	@JsonProperty("is_answered")
	private Boolean isAnswered;

	@JsonIgnoreProperties
	private Integer lastTaskID;

	@JsonProperty("is_finished")
	public Boolean isFinished() {
		return id.equals(lastTaskID);
	}


	public static final class Mapper implements RowMapper<PilotTask> {

		@Override
		public PilotTask mapRow(ResultSet rs, int rowNum) throws SQLException {
			final PilotTask task = new PilotTask();

			task.id = rs.getInt("task_id");
			task.points = rs.getInt("points");
			task.type = TaskType.valueOf(rs.getString("type"));
			rs.getBoolean("success");
			task.isAnswered = !rs.wasNull();
			task.lastTaskID = rs.getInt("last_task_id");

			return task;
		}
	}
}
