package legends.models;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RefreshTimer {

	public Integer teamID;
	public Integer taskID;
	public Integer duration;
	public Integer startTime;

	public static final class Mapper implements RowMapper<RefreshTimer> {

		@Override
		public RefreshTimer mapRow(ResultSet rs, int rowNum) throws SQLException {
			final RefreshTimer refresher = new RefreshTimer();
			refresher.teamID = rs.getInt("team_id");
			refresher.taskID = rs.getInt("task_id");
			refresher.duration = rs.getInt("duration");
			refresher.startTime = rs.getInt("start_time");
			return refresher;
		}
	}
}
