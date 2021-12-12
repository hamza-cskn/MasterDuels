package mc.obliviate.blokduels.utils;

import org.bukkit.Bukkit;

public class Logger {

	private static final boolean debugModeEnabled = false;

	public static void debug(String message) {
		if (!debugModeEnabled) return;
		Bukkit.getConsoleSender().sendMessage("[DEBUG] " + fixLength(message));
	}

	public static void severe(String message) {
		Bukkit.getLogger().severe(fixLength(message));
	}

	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage("[ERROR INFO] " + fixLength(message));
	}

	private static String fixLength(String string) {
		if (string.length() > 500) {
			return string.substring(0, 500) + "...";
		}
		return string;
	}

}
