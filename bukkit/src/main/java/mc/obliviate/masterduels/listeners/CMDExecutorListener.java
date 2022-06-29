package mc.obliviate.masterduels.listeners;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Purpose of this class,
 * executes console commands which is given in config.yml
 **/
public class CMDExecutorListener implements Listener {

	@EventHandler
	public void onDuelGameStart(DuelMatchStartEvent event) {
		for (String cmd : ConfigurationHandler.getConfig().getStringList("execute-console-commands.when-duel-started")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		event.getMatch().getGameTaskManager().repeatTask("debug", () -> {
			Player player = Bukkit.getPlayerExact("Mr_Obliviate");
			HCore.sendActionBar(player, ChatColor.RED + ChatColor.BOLD.toString() + event.getMatch().getMatchState().getMatchStateType());
		}, null, 0, 1);

	}

	@EventHandler
	public void onDuelGameEnd(DuelMatchEndEvent event) {
		for (String cmd : ConfigurationHandler.getConfig().getStringList("execute-console-commands.when-duel-ended")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

		}
	}


}
