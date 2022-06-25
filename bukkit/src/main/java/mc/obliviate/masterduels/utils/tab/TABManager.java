package mc.obliviate.masterduels.utils.tab;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;

public final class TABManager {

	private static boolean enabled = false;

	public TABManager(final MasterDuels plugin) {

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
