package mc.obliviate.blokduels.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent e) {
		if (e.getPlayer().getMetadata("forceTeleport") != null) {
			//Bukkit.broadcastMessage("uncancelling");
			//e.setCancelled(false);
		}
	}
}
