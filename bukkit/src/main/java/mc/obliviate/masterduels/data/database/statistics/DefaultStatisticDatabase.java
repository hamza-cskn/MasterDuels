package mc.obliviate.masterduels.data.database.statistics;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.playerdata.PlayerData;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.util.database.sql.ColumnValues;
import mc.obliviate.util.database.sql.SQLDatabase;
import mc.obliviate.util.database.sql.SQLDriverProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultStatisticDatabase extends SQLDatabase<DuelStatistic> implements StatisticsDatabase {

	public static final String TABLE_NAME = "statistics";
	public static final String USER_ID_COLUMN = "uuid";
	public static final String STATISTIC_COLUMN = "statistic";

	public DefaultStatisticDatabase(SQLDriverProvider provider) {
		super(provider);
	}

	@Override
	public List<ColumnValues> serialize(DuelStatistic statistic) {
		ColumnValues result = new ColumnValues();
		result.add("uuid", statistic.getPlayerUniqueId());
		result.add("statistics", HCore.serialize(statistic.getPlayerData()));
		return Collections.singletonList(result);
	}

	@Override
	public DuelStatistic deserialize(ResultSet rs) throws SQLException {
		UUID uuid = UUID.fromString(rs.getString(USER_ID_COLUMN));
		String serializedStatistics = rs.getString(STATISTIC_COLUMN);
		PlayerData playerData = HCore.deserialize(serializedStatistics, PlayerData.class);
		return new DuelStatistic(uuid, playerData);
	}

	@Override
	public String getId(DuelStatistic duelStatistic) {
		return duelStatistic.getPlayerUniqueId().toString();
	}

	@Override
	public String getIdColumn() {
		return USER_ID_COLUMN;
	}

	@Override
	public String getTable() {
		return TABLE_NAME;
	}

	@Override
	public CompletableFuture<DuelStatistic> loadStatistics(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> super.load(uuid));
	}

	@Override
	public CompletableFuture<Void> saveStatistics(DuelStatistic statistic) {
		return super.saveAsync(statistic);
	}

}
