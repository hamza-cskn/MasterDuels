package mc.obliviate.masterduels.utils;

import org.bukkit.Bukkit;

public class Logger {

	private static final boolean debugModeEnabled = true;

	public static void debug(String message) {
		if (!debugModeEnabled) return;
		Bukkit.getLogger().info("[MasterDuels] [DEBUG] " + fixLength(message));
	}

	public static void severe(String message) {
		Bukkit.getLogger().severe("[MasterDuels] " + fixLength(message));
	}

	public static void warn(String message) {
		Bukkit.getLogger().warning("[MasterDuels] " + fixLength(message));
	}

	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage("[MasterDuels] [ERROR INFO] " + fixLength(message));
	}

	private static String fixLength(String string) {
		if (string.length() > 500) {
			return string.substring(0, 500) + "...";
		}
		return string;
	}

}
