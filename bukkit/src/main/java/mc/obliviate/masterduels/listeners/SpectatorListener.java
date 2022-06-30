package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onItemClick(PlayerInteractEvent e) {
		final Spectator spectator = UserHandler.getSpectator(e.getPlayer().getUniqueId());
		if (spectator == null) return;

		e.setCancelled(true);
		if (e.getItem() != null && e.getItem().getType().equals(Material.BARRIER)) {
			spectator.getMatch().getMatchState().leave(spectator);
		}
	}
}
