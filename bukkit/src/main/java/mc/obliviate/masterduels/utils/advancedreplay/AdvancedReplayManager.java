package mc.obliviate.masterduels.utils.advancedreplay;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import me.jumper251.replay.api.ReplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class AdvancedReplayManager implements Listener {

	private static boolean enabled = false;

	public void init(MasterDuels plugin) {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("AdvancedReplay")) {
			enabled = true;
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}

	@EventHandler
	public void onMatchStart(DuelMatchStartEvent event) {
		ReplayAPI.getInstance().recordReplay("masterduels-autorecord-" + event.getMatch().getId(), event.getMatch().getPlayers().get(0), new ArrayList<>(event.getMatch().getPlayers()));
	}

	@EventHandler
	public void onMatchEnd(DuelMatchEndEvent event) {
		ReplayAPI.getInstance().stopReplay("masterduels-autorecord-" + event.getMatch().getId(), true);
	}
}
