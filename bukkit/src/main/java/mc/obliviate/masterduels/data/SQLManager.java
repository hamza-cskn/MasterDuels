package mc.obliviate.masterduels.data;

import com.hakan.core.HCore;
import mc.obliviate.bloksqliteapi.SQLHandler;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;
import mc.obliviate.bloksqliteapi.sqlutils.SQLUpdateColumn;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.playerdata.PlayerData;
import mc.obliviate.masterduels.playerdata.history.MatchHistoryLog;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.Utils;

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
    private static final Object[] objects = Utils.loadClass("mc.obliviate.masterduels.utils.initializer.MasterDuelsInitializer").getEnumConstants();

    public SQLManager(MasterDuels plugin) {
        super(plugin.getDataFolder().getPath());

        playerDataTable = new SQLTable("playerData", "uuid")
                .addField("uuid", DataType.TEXT)
                .addField("receivesInvites", DataType.INTEGER);

        statisticsTable = new SQLTable("statistics", "uuid")
                .addField("uuid", DataType.TEXT)
                .addField("statistics", DataType.TEXT);

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
        statisticsTable.create();
    }

    public static List<MatchHistoryLog> loadDuelHistories() {
        final List<MatchHistoryLog> list = new ArrayList<>();
        try {
            ResultSet rs = historyTable.selectAll();
            while (rs.next()) {
                try {
                    MatchHistoryLog log = HCore.deserialize(rs.getString("log"), MatchHistoryLog.class);
                    list.add(log);
                } catch (Exception exception) {
                    list.add(null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveDuelHistory(MatchHistoryLog log) {
        UUID uuid = log.getMatch() != null ? log.getMatch().getId() : UUID.randomUUID();
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
                    .putData("receivesInvites", (bool ? 0 : 1));
            playerDataTable.insert(update);
        }
        return !bool;
    }

    public static DuelStatistic deserializeStatistic(ResultSet rs, boolean emptyResultSet) {
        try {
            final UUID uuid = UUID.fromString(rs.getString("uuid"));
            final String serializedStatistics = rs.getString("statistics");
            PlayerData playerData = HCore.deserialize(serializedStatistics, PlayerData.class);
            if (emptyResultSet)
                while (rs.next()) ;
            return new DuelStatistic(uuid, playerData);

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

    public DuelStatistic getStatistic(final UUID uuid) {
        if (playerDataTable.exist(uuid.toString())) {
            final ResultSet rs = playerDataTable.select(uuid.toString());
            return deserializeStatistic(rs, true);
        }
        return DuelStatistic.createDefaultInstance(uuid);
    }

    public void saveAllStatistics() {
        for (IUser user : UserHandler.getUserMap().values()) {
            saveStatistic(user.getStatistic());
        }
    }

    public void saveStatistic(DuelStatistic statistic) {
        UUID uuid = statistic.getPlayerUniqueId() != null ? statistic.getPlayerUniqueId() : UUID.randomUUID();
        SQLUpdateColumn update = statisticsTable.createUpdate(uuid).putData("uuid", uuid).putData("statistics", HCore.serialize(statistic));
        statisticsTable.insert(update);
    }

    public LinkedList<DuelStatistic> getTopPlayers(String fieldName, int limit) throws SQLException {
        return deserializeStatisticsList(playerDataTable.getHighest(fieldName, limit));
    }

    public SQLTable getPlayerDataTable() {
        return playerDataTable;
    }

    public SQLTable getHistoryTable() {
        return historyTable;
    }

}
