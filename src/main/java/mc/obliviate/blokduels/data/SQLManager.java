package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.history.GameHistoryLog;
import mc.obliviate.blokduels.statistics.DuelStatistic;
import mc.obliviate.blokduels.utils.Logger;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.UUID;

public class SQLManager extends SQLHandler {

	private final SQLTable playerDataTable;
	private final SQLTable historyTable;

	public SQLManager(BlokDuels plugin) {
		super(plugin.getDataFolder().getPath());

		playerDataTable = new SQLTable("playerData", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("wins", DataType.INTEGER)
				.addField("loses", DataType.INTEGER)
				.addField("receivesInvites", DataType.INTEGER);

		historyTable = new SQLTable("history", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("winners", DataType.TEXT)
				.addField("losers", DataType.TEXT)
				.addField("startTime", DataType.INTEGER)
				.addField("endTime", DataType.INTEGER);
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

	public void appendDuelHistory(final GameHistoryLog log) {
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

	public GameHistoryLog getDuelHistory(final UUID uuid) throws SQLException {
		final ResultSet rs = historyTable.select(uuid.toString());

		rs.next();
		final GameHistoryLog log = SerializerUtils.deserializeGameHistoryLog(rs);
		while (rs.next()) { //empty result set.
			Logger.severe("Duplicated history found: " + uuid);
		}
		return log;
	}

	public LinkedList<GameHistoryLog> getAllLogs() throws SQLException {
		final ResultSet rs = sqlQuery("SELECT * FROM " + historyTable.getTableName() + " ORDER BY startTime DESC");
		final LinkedList<GameHistoryLog> logs = new LinkedList<>();
		while (rs.next()) {
			logs.add(SerializerUtils.deserializeGameHistoryLog(rs));
		}
		return logs;
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
			try {
				if (rs.next()) {
					final int wins = rs.getInt("wins");
					final int loses = rs.getInt("loses");
					while (rs.next()) {
						Logger.severe("Statistics duplication found: " + uuid);
					}
					return new DuelStatistic(uuid, wins, loses);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new DuelStatistic(uuid, 0, 0);
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


}
