package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.arena.DuelGameFinishEvent;
import mc.obliviate.masterduels.api.events.arena.DuelGameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

//executes console commands which is given in config.yml
public class CMDExecutorListener implements Listener {

	private final MasterDuels plugin;

	public CMDExecutorListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDuelGameStart(DuelGameStartEvent event) {
		for (String cmd : plugin.getDatabaseHandler().getConfig().getStringList("execute-console-commands.when-duel-started")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

	@EventHandler
	public void onDuelGameEnd(DuelGameFinishEvent event) {
		for (String cmd : plugin.getDatabaseHandler().getConfig().getStringList("execute-console-commands.when-duel-ended")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

		}
	}


}
