package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.spectator.Spectator;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onItemClick(PlayerInteractEvent e) {
		final IUser user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Spectator) {
			e.setCancelled(true);
			if (e.getItem() != null && e.getItem().getType().equals(Material.BARRIER)) {
				user.getGame().leave((Spectator) user);
			}
		}
	}
}
