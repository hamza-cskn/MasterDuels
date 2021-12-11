package mc.obliviate.blokduels.utils;

import org.bukkit.Bukkit;

public class Logger {

	private static final boolean debugModeEnabled = false;

	public static void debug(String message) {
		if (!debugModeEnabled) return;
		Bukkit.getConsoleSender().sendMessage("[DEBUG] " + message);
	}

	public static void severe(String message) {
		Bukkit.getLogger().severe(message);
	}

	public static void error(String message) {
		Bukkit.getConsoleSender().sendMessage("[ERROR INFO] " + message);
	}

}
