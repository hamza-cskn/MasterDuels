package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.api.events.arena.DuelMatchUninstallEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaClearListener implements Listener {

	private final MasterDuels plugin;

	public ArenaClearListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onGameStart(final DuelMatchStartEvent event) {
		plugin.getArenaClearHandler().add(event.getMatch(), plugin);
	}

	@EventHandler
	public void onGameEnd(final DuelMatchUninstallEvent event) {
		plugin.getArenaClearHandler().getArenaClear(event.getMatch().getArena().getName()).clear();
	}

}
