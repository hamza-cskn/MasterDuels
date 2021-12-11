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
			try {
				Class.forName("me.neznamy.tab.shared.TAB");
				enabled = true;
				plugin.getLogger().info("TAB plugin found. The API successfully hooked.");
			} catch (ClassNotFoundException ignored) {}
		}



	}

	public static boolean isEnabled() {
		return enabled;
	}
}
