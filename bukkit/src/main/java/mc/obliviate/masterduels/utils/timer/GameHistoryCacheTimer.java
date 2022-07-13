package mc.obliviate.masterduels.utils.timer;

import mc.obliviate.masterduels.MasterDuels;

public class GameHistoryCacheTimer implements Timer {
	@Override
	public void init(MasterDuels plugin) {

	}

	/*
	@Override
	public void init(final MasterDuels plugin) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,() -> {
			try {
				//update cache
				MatchHistoryLog.historyCache.clear();
				MatchHistoryLog.historyCache.addAll(plugin.getSqlManager().getAllLogs());

				//trim cache
				plugin.getSqlManager().clearOldHistories(ConfigurationHandler.getConfig().getInt("game-history.max-amount-of-histories-in-storage"));
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}, 0, ConfigurationHandler.getConfig().getInt("game-history.cache-refresh"));

	}

	 */


}
