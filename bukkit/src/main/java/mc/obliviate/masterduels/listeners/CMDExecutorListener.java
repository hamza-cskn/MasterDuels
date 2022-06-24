package mc.obliviate.masterduels.listeners;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.arena.DuelGameFinishEvent;
import mc.obliviate.masterduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.masterduels.data.YamlStorageHandler;
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

	private final MasterDuels plugin;

	public CMDExecutorListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDuelGameStart(DuelGameStartEvent event) {
		for (String cmd : YamlStorageHandler.getConfig().getStringList("execute-console-commands.when-duel-started")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		event.getGame().getGameTaskManager().repeatTask("debug", () -> {
			Player player = Bukkit.getPlayerExact("Mr_Obliviate");
			HCore.sendActionBar(player, ChatColor.RED + ChatColor.BOLD.toString() + event.getGame().getGameState().getGameStateType());
		}, null, 0, 1);

	}

	@EventHandler
	public void onDuelGameEnd(DuelGameFinishEvent event) {
		for (String cmd : YamlStorageHandler.getConfig().getStringList("execute-console-commands.when-duel-ended")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

		}
	}


}
