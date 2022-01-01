package mc.obliviate.blokduels.utils.tab;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.utils.Logger;
import org.bukkit.Bukkit;

public class TABManager {

	private static boolean enabled = false;

	public TABManager(final BlokDuels plugin) {

		if (!plugin.getDatabaseHandler().getConfig().getBoolean("bossbars.enabled")) return;

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("TAB")) {
			try {
				Class.forName("me.neznamy.tab.api.TabAPI");
				enabled = true;
				plugin.getLogger().info("TAB plugin found. The API successfully hooked.");
			} catch (ClassNotFoundException ignored) {
				Logger.error("TAB plugin enabled but TAB library could not found.");
			}
		}



	}

	public static boolean isEnabled() {
		return enabled;
	}
}
