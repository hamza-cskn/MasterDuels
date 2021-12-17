package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.history.GameHistoryLog;
import mc.obliviate.blokduels.utils.Logger;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLManager extends mc.obliviate.bloksqliteapi.SQLHandler {

	private final BlokDuels plugin;
	private final SQLTable statisticsTable;
	private final SQLTable historyTable;

	public SQLManager(BlokDuels plugin) {
		super(plugin.getDataFolder().getPath(), true);
		this.plugin = plugin;

		statisticsTable = new SQLTable("statistics", "uuid")
				.addField("uuid", DataType.TEXT)
				.addField("wins", DataType.INTEGER)
				.addField("loses", DataType.INTEGER);

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

		statisticsTable.create();
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
		while(rs.next()) {
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

	public List<GameHistoryLog> getAllLogs() throws SQLException {
		final ResultSet rs = historyTable.selectAll();
		final List<GameHistoryLog> logs = new ArrayList<>();
		while (rs.next()) {
			logs.add(SerializerUtils.deserializeGameHistoryLog(rs));
		}
		return logs;
	}

	public void addWin(final UUID uuid, int amount) {
		increaseValue(uuid, amount, "wins");
	}

	public void addLose(final UUID uuid, int amount) {
		increaseValue(uuid, amount, "loses");
	}

	private void increaseValue(final UUID uuid, final int amount, final String type) {
		if (statisticsTable.exist(uuid.toString())) {
			sqlUpdate("UPDATE " + statisticsTable.getTableName() + " SET " + type + " = " + type + " + " + amount + " WHERE id = " + uuid);
		} else {
			final SQLUpdateColumn update = statisticsTable.createUpdate(uuid.toString())
					.putData("uuid", uuid.toString())
					.putData("wins", 0)
					.putData("loses", 0)
					.putData(type, amount); //replace type value

			statisticsTable.insertOrUpdate(update);
		}
	}


}
