package mc.obliviate.masterduels.data.database.statistics;

import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StatisticsDatabase {

	default void connect() {

	}

	default void disconnect() {

	}

	CompletableFuture<DuelStatistic> loadStatistics(UUID uuid);

	CompletableFuture<Void> saveStatistics(DuelStatistic statistic);

}
