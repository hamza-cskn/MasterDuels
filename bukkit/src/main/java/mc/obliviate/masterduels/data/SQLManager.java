package mc.obliviate.masterduels.data;

import com.hakan.core.HCore;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.history.MatchHistoryLog;
import mc.obliviate.masterduels.statistics.DuelStatistic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SQLManager extends SQLHandler {

    private static SQLTable playerDataTable;
    private static SQLTable historyTable;
    private static SQLTable statisticsTable;
    private static final Object[] objects = Utils.loadClass("mc.obliviate.masterduels.MasterDuelsInitializer").getEnumConstants();


	public SQLManager(MasterDuels plugin) {
		super(plugin.getDataFolder().getPath());

		playerDataTable = new SQLTable("playerData", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("wins", DataType.INTEGER)
				.addField("loses", DataType.INTEGER)
				.addField("receivesInvites", DataType.INTEGER);

		historyTable = new SQLTable("history", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("log", DataType.TEXT);
	}

	public void init() {
		connect("database");
	}

	@Override
	public void onConnect() {
		super.onConnect();

		playerDataTable.create();
		historyTable.create();
	}

	public static DuelStatistic deserializeStatistic(ResultSet rs, boolean emptyResultSet) {
		try {
			final UUID uuid = UUID.fromString(rs.getString("uuid"));
			final int wins = rs.getInt("wins");
			final int loses = rs.getInt("loses");
			if (emptyResultSet)
				while (rs.next()) ;
			return new DuelStatistic(uuid, wins, loses);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*public void appendDuelHistory(final MatchHistoryLog log) {
		final SQLUpdateColumn update = historyTable.createUpdate(log.getUuid())
				.putData("uuid", log.getUuid())
				.putData("winners", SerializerUtils.serializeStringConvertableList(log.getWinners()))
				.putData("losers", SerializerUtils.serializeStringConvertableList(log.getLosers()))
				.putData("startTime", log.getStartTime())
				.putData("endTime", log.getEndTime());
		historyTable.insertOrUpdate(update);
	}

	public void clearOldHistories(final int limit) throws SQLException {
		final ResultSet rs = historyTable.getHighest("startTime");
		int amount = 0;
		while (rs.next()) {
			amount++;
			if (amount > limit) {
				rs.deleteRow();
			}
		}
	}

	public MatchHistoryLog getDuelHistory(final UUID uuid) throws SQLException {
		final ResultSet rs = historyTable.select(uuid.toString());

		rs.next();
		final MatchHistoryLog log = SerializerUtils.deserializeGameHistoryLog(rs);
		while (rs.next()) { //empty result set.
			Logger.severe("Duplicated history found: " + uuid);
		}
		return log;
	}

	public LinkedList<MatchHistoryLog> getAllLogs() throws SQLException {
		final ResultSet rs = sqlQuery("SELECT * FROM " + historyTable.getTableName() + " ORDER BY startTime DESC");
		final LinkedList<MatchHistoryLog> logs = new LinkedList<>();
		while (rs.next()) {
			logs.add(SerializerUtils.deserializeGameHistoryLog(rs));
		}
		return logs;
	}

	 */

	public static List<MatchHistoryLog> loadDuelHistories() {
		final List<MatchHistoryLog> list = new ArrayList<>();
		try {
			ResultSet rs = historyTable.selectAll();
			while (rs.next()) {
				MatchHistoryLog log = HCore.deserialize(rs.getString("log"), MatchHistoryLog.class);
				list.add(log);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void saveDuelHistory(MatchHistoryLog log) {
		UUID uuid = log.getMatch() == null ? log.getMatch().getId() : UUID.randomUUID();
		SQLUpdateColumn update = historyTable.createUpdate(uuid).putData("uuid", uuid).putData("log", HCore.serialize(log));
		historyTable.insert(update);
	}

	public boolean getReceivesInvites(final UUID uuid) {
		final Integer value = playerDataTable.getInteger(uuid.toString(), "receivesInvites");
		return value == null || value == 1;
	}

	/**
	 * @return new state
	 */
	public boolean toggleReceivesInvites(final UUID uuid) {
		final boolean bool = getReceivesInvites(uuid);
		if (playerDataTable.exist(uuid.toString())) {
			sqlUpdate("UPDATE " + playerDataTable.getTableName() + " SET receivesInvites = " + (bool ? 0 : 1) + " WHERE uuid = '" + uuid + "'");
		} else {
			final SQLUpdateColumn update = playerDataTable.createUpdate(uuid.toString())
					.putData("uuid", uuid.toString())
					.putData("wins", 0)
					.putData("loses", 0)
					.putData("receivesInvites", (bool ? 0 : 1));
			playerDataTable.insert(update);
		}
		return !bool;
	}

	public DuelStatistic getStatistic(final UUID uuid) {
		if (playerDataTable.exist(uuid.toString())) {
			final ResultSet rs = playerDataTable.select(uuid.toString());
			return deserializeStatistic(rs, true);
		}
		return DuelStatistic.createDefaultInstance(uuid);
	}

	public LinkedList<DuelStatistic> getTopPlayers(String fieldName, int limit) {
		final ResultSet rs = playerDataTable.getHighest(fieldName, limit);
		final LinkedList<DuelStatistic> result = new LinkedList<>();
		try {
			while (rs.next()) {
				final UUID uuid = UUID.fromString(rs.getString("uuid"));
				final int wins = rs.getInt("wins");
				final int loses = rs.getInt("loses");
				result.add(new DuelStatistic(uuid, wins, loses));
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LinkedList<DuelStatistic> deserializeStatisticsList(ResultSet rs) throws SQLException {
		final LinkedList<DuelStatistic> result = new LinkedList<>();
		while (rs.next()) {
			result.add(deserializeStatistic(rs, false));
		}
		return result;
	}


	public void addWin(final UUID uuid, int amount) {
		increaseValue(uuid, amount, "wins");
	}

	public void addLose(final UUID uuid, int amount) {
		increaseValue(uuid, amount, "loses");
	}

	private void increaseValue(final UUID uuid, final int amount, final String type) {
		if (playerDataTable.exist(uuid.toString())) {
			sqlUpdate("UPDATE " + playerDataTable.getTableName() + " SET " + type + " = " + type + " + " + amount + " WHERE uuid = '" + uuid + "'");
		} else {
			final SQLUpdateColumn update = playerDataTable.createUpdate(uuid.toString())
					.putData("uuid", uuid.toString())
					.putData("wins", 0)
					.putData("loses", 0)
					.putData("receivesInvites", 1)
					.putData(type, amount); //replace type value

			playerDataTable.insert(update);
		}
	}

	public SQLTable getPlayerDataTable() {
		return playerDataTable;
	}

	public SQLTable getHistoryTable() {
		return historyTable;
	}

}
