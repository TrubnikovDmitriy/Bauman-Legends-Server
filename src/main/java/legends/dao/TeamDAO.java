package legends.dao;

import legends.exceptions.CriticalInternalError;
import legends.exceptions.TaskIsNotExist;
import legends.exceptions.TeamDoesNotExist;
import legends.models.*;
import legends.requestviews.Answer;
import legends.requestviews.SecretKey;
import legends.responseviews.KeyAnswer;
import legends.responseviews.TeamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Repository
public class TeamDAO {

	private final JdbcTemplate jdbcTemplate;
	private final Logger logger;

	public TeamDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.logger = LoggerFactory.getLogger(TeamDAO.class);
	}

	public List<TeamForTable> getTeams(final boolean fullList) {
		if (fullList) {
			return jdbcTemplate.query(
					"SELECT " +
							"  id, name, score, leader_name, final_tasks_arr, " +
							"  start_time, finish_time, finished, started, fails_count, " +
							"  (SELECT COUNT(ct.task_id) FROM current_tasks AS ct WHERE ct.team_id=tms.id AND ct.type=?) AS task_count " +
							"FROM old_teams AS tms JOIN auth a ON tms.id=a.team_id WHERE type=?",
					new Object[] { TaskTypeOld.FINAL.name(), TeamType.PLAYER.name() },
					new TeamForTable.Mapper()
			);
		} else {
			return jdbcTemplate.query(
					"SELECT " +
							"  id, name, score, leader_name, final_tasks_arr, " +
							"  start_time, finish_time, finished, started, fails_count, " +
							"  (SELECT COUNT(ct.task_id) FROM current_tasks AS ct WHERE ct.team_id=tms.id AND ct.type=?) AS task_count " +
							"FROM old_teams AS tms JOIN auth a ON tms.id=a.team_id " +
							"WHERE type=? AND started=TRUE AND finished=FALSE",
					new Object[] { TaskTypeOld.FINAL.name(), TeamType.PLAYER.name() },
					new TeamForTable.Mapper()
			);
		}
	}

	public TeamInfo getTeamForModerator(final Integer teamID) {
		try {
			final List<String> members = jdbcTemplate.query(
					"SELECT first_name, second_name FROM players WHERE team_id=?",
					new Object[]{teamID},
					(rs, n) -> rs.getString(1) + ' ' + rs.getString(2)
			);

			final List<Tooltip> tooltips = jdbcTemplate.query(
					"SELECT number, tooltip FROM current_tasks ctsk " +
							"  JOIN old_tasks tsk ON ctsk.task_id = tsk.id " +
							"  JOIN statues st ON tsk.statue_number = st.number " +
							"WHERE ctsk.team_id=? AND ctsk.type=? AND ctsk.success IS TRUE " +
							"ORDER BY number;",
					new Object[]{teamID, TaskTypeOld.EXTRA.name()},
					(rs, i) -> new Tooltip(rs.getInt(1), rs.getString(2))
			);

			return jdbcTemplate.queryForObject(
					"SELECT t.id id, name, leader_name, score, start_time, " +
							"a.login login, a.pass pass FROM old_teams AS t " +
							"JOIN auth AS a ON t.id = a.team_id WHERE t.id=?",
					new Object[]{teamID},
					new TeamInfo.Mapper(members, tooltips)
			);

		} catch (EmptyResultDataAccessException e) {
			throw new TeamDoesNotExist(e, teamID);
		}
	}

	public TeamInfo getTeamForPlayer(final Integer teamID) {
		return getTeamForModerator(teamID).eraseLoginPass();
	}

	public List<Router> getRouters(final boolean fullList) {
		final HashMap<Integer, List<Trail>> map = new HashMap<>();
		if (fullList) {
			jdbcTemplate.query(
					"SELECT" +
							"  tsk.id task_id, tms.id team_id, cur.start_time, " +
							"  cur.finish_time, success, duration, content " +
							"FROM old_teams tms " +
							"  JOIN old_tasks tsk ON tsk.id=ANY(tms.final_tasks_arr::int[]) " +
							"  FULL JOIN current_tasks cur ON tms.id=cur.team_id AND tsk.id = cur.task_id;",
					new HashMapper(map)
			);
			return jdbcTemplate.query(
					"SELECT old_teams.id, name, COUNT(players.id) AS players_count, final_tasks_arr, fails_count, start_time " +
							"FROM old_teams JOIN players ON old_teams.id=players.team_id GROUP BY old_teams.id",
					new Router.Mapper(map)
			);
		} else {
			jdbcTemplate.query(
					"SELECT" +
							"  tsk.id task_id, tms.id team_id, cur.start_time, " +
							"  cur.finish_time, success, duration, content " +
							"FROM old_teams tms " +
							"  JOIN old_tasks tsk ON tsk.id=ANY(tms.final_tasks_arr::int[]) " +
							"  FULL JOIN current_tasks cur ON tms.id=cur.team_id AND tsk.id = cur.task_id " +
							"WHERE tms.started=TRUE AND tms.finished=FALSE",
					new HashMapper(map)
			);
			return jdbcTemplate.query(
					"SELECT old_teams.id, name, COUNT(players.id) AS players_count, final_tasks_arr, fails_count, start_time " +
							"FROM old_teams JOIN players ON old_teams.id=players.team_id " +
							"WHERE started=TRUE and finished=FALSE GROUP BY old_teams.id",
					new Router.Mapper(map)
			);
		}
	}

	public void validateAnswer(final Answer answer) {
		try {
			jdbcTemplate.queryForObject(
					"SELECT id FROM current_tasks WHERE " +
							"team_id=? AND task_id=? AND type=? AND success IS NULL;",
					new Object[] {
							answer.getTeamID(),
							answer.getTaskID(),
							answer.getTaskTypeOld().name()
					},
					Integer.class
			);

		} catch (EmptyResultDataAccessException ignore) {
			throw new TaskIsNotExist();
		}
	}

	public List<Integer> getOpenedStatues(final Integer teamID) {
		return jdbcTemplate.query(
				"SELECT statue_number FROM extra_points WHERE team_id=? AND used=TRUE",
				new Object[] { teamID },
				(rs, i) -> rs.getInt(1)
		);
	}

	@Transactional
	public synchronized KeyAnswer checkKey(final SecretKey key) {

		try {
			// Check that such code exists for this command
			final KeyAnswer answer = jdbcTemplate.queryForObject(
					"SELECT points, statue_number FROM extra_points " +
							"WHERE team_id=? AND secret_key=? AND used=FALSE",
					new Object[]{key.getTeamID(), key.getKey()},
					new KeyAnswer.Mapper()
			);
			if (answer == null) throw new CriticalInternalError(getClass().getSimpleName());

			// Mark the code as 'used'
			jdbcTemplate.update(
					"UPDATE extra_points SET used=TRUE WHERE team_id=? AND secret_key=?",
					key.getTeamID(), key.getKey()
			);

			// Increase score of team
			jdbcTemplate.update(
					"UPDATE old_teams SET score=score+? WHERE id=?",
					answer.getPoints(), key.getTeamID()
			);

			answer.setAccept(true);
			return answer;

		} catch (EmptyResultDataAccessException ignore) {
			final KeyAnswer answer = new KeyAnswer();
			answer.setAccept(false);
			return answer;
		}
	}


	private static class HashMapper implements RowMapper<Object> {
		final RowMapper<Trail> mapper = new Trail.Mapper();
		private final HashMap<Integer, List<Trail>> map;

		HashMapper(@NotNull HashMap<Integer, List<Trail>> map) {
			this.map = map;
		}

		@Nullable
		@Override
		public Object mapRow(ResultSet rs, int i) throws SQLException {
			final Integer teamID = rs.getInt("team_id");
			final List<Trail> trails = map.getOrDefault(teamID, new LinkedList<>());
			trails.add(mapper.mapRow(rs, i));
			map.put(teamID, trails);
			return null;
		}
	}
}
