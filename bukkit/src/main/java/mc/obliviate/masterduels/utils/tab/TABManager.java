package mc.obliviate.masterduels.utils.tab;

import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;

public final class TABManager {

	private static boolean enabled = false;

	public void init() {
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("TAB")) {
			try {
				Class.forName("me.neznamy.tab.api.TabAPI");
				enabled = true;
			} catch (ClassNotFoundException ignored) {
				Logger.error("TAB plugin found but its library could not.");
			}
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}
}
