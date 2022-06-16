package mc.obliviate.masterduels.utils;

import org.bukkit.Bukkit;

public class Logger {

	private static boolean debugModeEnabled = false;
	private static DebugPart debugPart = null;

	public static void debug(DebugPart part, String message) {
		if (!debugModeEnabled) return;
		if (part.equals(debugPart)) return;
		debug(message);
	}

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

	public static DebugPart getDebugPart() {
		return debugPart;
	}

	public static void setDebugPart(DebugPart debugPart) {
		Logger.debugPart = debugPart;
	}

	public static void setDebugModeEnabled(boolean debugModeEnabled) {
		Logger.debugModeEnabled = debugModeEnabled;
	}

	public static boolean isDebugModeEnabled() {
		return debugModeEnabled;
	}

	public enum DebugPart {
		GAME
	}
}
