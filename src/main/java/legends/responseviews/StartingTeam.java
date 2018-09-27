package legends.responseviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StartingTeam {

	@JsonProperty("team_id")
	private Integer teamID;

	@JsonProperty("team_name")
	private String teamName;

	@JsonProperty("start_time")
	private String startTime;

	@JsonProperty("started")
	private Boolean started;

	@JsonProperty("finished")
	private Boolean finished;

	@JsonProperty("players_count")
	private Integer playersCount;

	@JsonProperty("extra_task_count")
	private Integer extraTaskCount;

	@JsonProperty("photo_task_count")
	private Integer photoTaskCount;


	public static final class Mapper implements RowMapper<StartingTeam> {

		@Override
		public StartingTeam mapRow(ResultSet rs, int rowNum) throws SQLException {
			final StartingTeam team = new StartingTeam();

			team.teamID = rs.getInt("id");
			team.teamName = rs.getString("name");
			team.startTime = rs.getString("start_time");
			team.started = rs.getBoolean("started");
			team.finished = rs.getBoolean("finished");
			team.playersCount = rs.getInt("players_count");
			team.photoTaskCount = rs.getInt("photo_count");
			team.extraTaskCount = rs.getInt("extra_count");

			return team;
		}
	}
}
