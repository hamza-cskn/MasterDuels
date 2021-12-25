package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.Spectator;
import mc.obliviate.blokduels.user.User;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onItemClick(PlayerInteractEvent e) {
		final User user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Spectator) {
			e.setCancelled(true);
			if (e.getItem() != null && e.getItem().getType().equals(Material.BARRIER)) {
				user.getGame().leave((Spectator) user);
			}
		}
	}

}
