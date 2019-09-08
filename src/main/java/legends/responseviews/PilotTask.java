package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskTypeOld;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class PilotTask {

	@JsonProperty("task_id")
	private Integer id;

	@JsonProperty("task_type")
	private TaskTypeOld type;

	@JsonProperty("points")
	private Integer points;

	@JsonProperty("is_answered")
	private Boolean isAnswered;

	@JsonIgnoreProperties
	private Integer lastTaskID;

	@JsonProperty("is_finished")
	public Boolean isFinished() {
		return id.equals(lastTaskID) && isAnswered;
	}


	public static final class Mapper implements RowMapper<PilotTask> {

		@Override
		public PilotTask mapRow(ResultSet rs, int rowNum) throws SQLException {
			final PilotTask task = new PilotTask();

			task.id = rs.getInt("task_id");
			task.points = rs.getInt("points");
			task.type = TaskTypeOld.valueOf(rs.getString("type"));
			rs.getBoolean("success");
			task.isAnswered = !rs.wasNull();
			task.lastTaskID = rs.getInt("last_task_id");

			return task;
		}
	}
}
