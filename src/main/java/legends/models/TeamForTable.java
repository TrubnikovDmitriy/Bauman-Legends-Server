package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import legends.Configuration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TeamForTable {

	@JsonProperty("team_id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("leader_name")
	private String leaderName;

	@JsonProperty("score")
	private Integer score;

	@JsonProperty("start_time")
	private Integer startTime;

	@JsonProperty("finish_time")
	private Integer finishTime;

	@JsonProperty("fails_count")
	private Integer failsCount;

	@JsonProperty("total_number_of_tasks")
	private Integer totalNumberOfTask;

	@JsonProperty("number_of_tasks")
	private Integer numberOfTask;

	@JsonProperty("is_active")
	private Boolean isActive;

	public static final class Mapper implements RowMapper<TeamForTable> {

		@Override
		public TeamForTable mapRow(ResultSet rs, int rowNum) throws SQLException {
			final TeamForTable postModel = new TeamForTable();
			postModel.id = rs.getInt("id");
			postModel.name = rs.getString("name");
			postModel.score = rs.getInt("score");
			postModel.leaderName = rs.getString("leader_name");
			postModel.startTime = rs.getInt("start_time");
			postModel.finishTime = rs.getInt("finish_time");
			postModel.failsCount = rs.getInt("fails_count");
			postModel.isActive = rs.getBoolean("started") && !rs.getBoolean("finished");

			// Calculate the progress
			if (Configuration.finalStage) {
				final Array arraySQL = rs.getArray("final_tasks_arr");
				final List<Integer> finalTasksArr = Arrays.asList(
						(Integer[]) arraySQL.getArray());

				postModel.totalNumberOfTask = finalTasksArr.size();
				postModel.numberOfTask = rs.getInt("task_count");

			} else {
				postModel.totalNumberOfTask = 0;
				postModel.numberOfTask = 0;
			}

			return postModel;
		}
	}
}
