package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.data.DataHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onItemClick(PlayerInteractEvent e) {
		final ISpectator spectator = DataHandler.getSpectator(e.getPlayer().getUniqueId());
		if (spectator == null) return;

		e.setCancelled(true);
		if (e.getItem() != null && e.getItem().getType().equals(Material.BARRIER)) {
			spectator.getGame().getMatchState().leave(spectator);
		}
	}
}
