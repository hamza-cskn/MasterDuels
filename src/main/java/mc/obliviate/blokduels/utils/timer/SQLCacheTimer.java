package mc.obliviate.blokduels.utils.timer;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.history.GameHistoryLog;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class SQLCacheTimer implements Timer {


	@Override
	public void init(final BlokDuels plugin) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,() -> {
			try {
				//update cache
				GameHistoryLog.historyCache.clear();
				GameHistoryLog.historyCache.addAll(plugin.getSqlManager().getAllLogs());

				//trim cache
				plugin.getSqlManager().clearOldHistories(plugin.getDatabaseHandler().getConfig().getInt("game-history.max-amount-of-histories-in-storage"));
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		},0, plugin.getDatabaseHandler().getConfig().getInt("game-history.cache-refresh"));

	}


}
