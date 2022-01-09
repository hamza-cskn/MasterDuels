package mc.obliviate.masterduels.commands;

import mc.obliviate.masterduels.MasterDuels;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DeveloperCMD implements Listener {

	private final MasterDuels plugin;

	public DeveloperCMD(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/masterduels")) {
			event.setCancelled(true);

			event.getPlayer().sendMessage("Master Duels v" + plugin.getDescription().getVersion() + " installed on " + Bukkit.getBukkitVersion());
			event.getPlayer().sendMessage("Server Brand: " + Bukkit.getVersion());
			event.getPlayer().sendMessage("Developed by:" + ChatColor.GREEN + " Mr_Obliviate");
			event.getPlayer().sendMessage("Edition:" + ChatColor.BLUE + ChatColor.ITALIC + " dev");
		}
	}

}
