package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import legends.models.TaskTypeOld;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class FinalTask {

	@JsonProperty("task_id")
	private int id;

	@JsonProperty("task_type")
	private TaskTypeOld type;

	@JsonProperty("start_time")
	private int startTime;

	@JsonProperty("duration")
	private int duration;

	@JsonProperty("points")
	private int points;

	@JsonProperty("is_answered")
	private Boolean isAnswered;

	@JsonIgnoreProperties
	private int lastTaskID;

	@JsonProperty("is_finished")
	public Boolean isFinished() {
		return (id == lastTaskID) && isAnswered;
	}

	@JsonIgnore
	public int getID() {
		return id;
	}

	@JsonIgnore
	public int getLastTaskID() {
		return lastTaskID;
	}

	public static final class Mapper implements RowMapper<FinalTask> {

		@Override
		public FinalTask mapRow(ResultSet rs, int rowNum) throws SQLException {
			final FinalTask task = new FinalTask();

			task.id = rs.getInt("task_id");
			task.startTime = rs.getInt("start_time");
			task.duration = rs.getInt("duration");
			task.points = rs.getInt("points");
			task.type = TaskTypeOld.valueOf(rs.getString("type"));
			rs.getBoolean("success");
			task.isAnswered = !rs.wasNull();
			task.lastTaskID = rs.getInt("last_task_id");

			return task;
		}
	}
}
