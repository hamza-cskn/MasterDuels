package mc.obliviate.blokduels.utils.tab;

import mc.obliviate.blokduels.BlokDuels;
import org.bukkit.Bukkit;

public class TABManager {

	private final BlokDuels plugin;
	private static boolean enabled = false;

	public TABManager(final BlokDuels plugin) {
		this.plugin = plugin;

		//plugin.getDatabaseHandler().getConfig().getBoolean("scoreboard.is-enabled")
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("TAB")) {
			enabled = true;
			plugin.getLogger().info("TitleManager plugin found. The API successfully hooked.");
		}



	}

	public static boolean isEnabled() {
		return enabled;
	}
}
