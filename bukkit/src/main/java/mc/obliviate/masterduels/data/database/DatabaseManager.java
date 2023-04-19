package mc.obliviate.masterduels.data.database;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.database.statistics.DefaultStatisticDatabase;
import mc.obliviate.masterduels.data.database.statistics.StatisticsDatabase;
import mc.obliviate.masterduels.data.database.user.DefaultUserDatabase;
import mc.obliviate.masterduels.data.database.user.UserDatabase;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.User;
import mc.obliviate.util.database.sql.SQLiteProvider;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

	private final StatisticsDatabase statisticsDatabase;
	private final UserDatabase userDatabase;

	public DatabaseManager(StatisticsDatabase statisticsDatabase, UserDatabase userDatabase) {
		this.statisticsDatabase = statisticsDatabase;
		this.userDatabase = userDatabase;
	}

	public static DatabaseManager createDefaultInstance() {
		SQLiteProvider sqLiteProvider = new SQLiteProvider(MasterDuels.getInstance().getDataFolder().getPath() + File.separator + "database.db");
		DefaultStatisticDatabase statisticDatabase = new DefaultStatisticDatabase(sqLiteProvider);

		return new DatabaseManager(
				statisticDatabase,
				new DefaultUserDatabase(sqLiteProvider, statisticDatabase));
	}

	public CompletableFuture<IUser> loadUser(UUID uuid) {
		return userDatabase.loadUser(uuid);
	}

	public CompletableFuture<DuelStatistic> loadStatistics(UUID uuid) {
		return statisticsDatabase.loadStatistics(uuid);
	}

	public CompletableFuture<Void> saveUser(IUser user) {
		return userDatabase.saveUser(user);
	}

	public CompletableFuture<Void> saveStatistics(DuelStatistic statistic) {
		return statisticsDatabase.saveStatistics(statistic);
	}

	public void connect() {
		statisticsDatabase.connect();
		userDatabase.connect();
	}

	public void disconnect() {
		userDatabase.disconnect();
		statisticsDatabase.disconnect();
	}


}
