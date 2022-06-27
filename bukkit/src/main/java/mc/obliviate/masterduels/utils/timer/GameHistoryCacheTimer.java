package mc.obliviate.masterduels.utils.timer;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.history.MatchHistoryLog;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class GameHistoryCacheTimer implements Timer {


	@Override
	public void init(final MasterDuels plugin) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,() -> {
			try {
				//update cache
				MatchHistoryLog.historyCache.clear();
				MatchHistoryLog.historyCache.addAll(plugin.getSqlManager().getAllLogs());

				//trim cache
				plugin.getSqlManager().clearOldHistories(YamlStorageHandler.getConfig().getInt("game-history.max-amount-of-histories-in-storage"));
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}, 0, YamlStorageHandler.getConfig().getInt("game-history.cache-refresh"));

	}


}
