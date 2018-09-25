package legends.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Router {

	@JsonProperty("team_id")
	private Integer teamID;

	@JsonProperty("team_name")
	private String teamName;

	@JsonProperty("players_count")
	private Integer playerCount;

	@JsonProperty("tasks_list_ids")
	private List<Integer> tasksIDs;

	@JsonProperty("fail_count")
	private Integer failsCount;

	@JsonProperty("start_time")
	private Integer startTime;

	@JsonProperty("tasks_list")
	private List<Trail> tasks;


	public static final class Mapper implements RowMapper<Router> {

		private final HashMap<Integer, List<Trail>> map;

		public Mapper(HashMap<Integer, List<Trail>> map) {
			this.map = map;
		}

		@Override
		public Router mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Router router = new Router();
			router.teamID = rs.getInt("id");
			router.teamName = rs.getString("name");
			router.playerCount = rs.getInt("players_count");
			router.failsCount = rs.getInt("fails_count");
			router.startTime = rs.getInt("start_time");
			router.tasksIDs = Arrays.asList(
					(Integer[]) rs.getArray("final_tasks_arr").getArray());
			router.tasks = map.getOrDefault(router.teamID, Collections.emptyList());
			return router;
		}
	}
}
