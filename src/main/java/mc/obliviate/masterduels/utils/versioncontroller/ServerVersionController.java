package mc.obliviate.masterduels.utils.versioncontroller;

import org.bukkit.Bukkit;

public enum ServerVersionController {
	UNKNOWN,
	OUTDATED,
	V1_8,
	V1_9,
	V1_10,
	V1_11,
	V1_12,
	V1_13,
	V1_14,
	V1_15,
	v1_16,
	v1_17,
	v1_18,
	NEWER;


	public static boolean isServerVersionAbove(ServerVersionController version) {
		return getServerVersion().ordinal() > version.ordinal();
	}

	public static boolean isServerVersionAtLeast(ServerVersionController version) {
		return getServerVersion().ordinal() >= version.ordinal();
	}

	public static boolean isServerVersionAtOrBelow(ServerVersionController version) {
		return getServerVersion().ordinal() <= version.ordinal();
	}

	public static boolean isServerVersionBelow(ServerVersionController version) {
		return getServerVersion().ordinal() < version.ordinal();
	}

	private static ServerVersionController serverVersion;

	public static ServerVersionController getServerVersion() {
		if (serverVersion == null) serverVersion = calculateServerVersion();
		return serverVersion;
	}

	private static ServerVersionController calculateServerVersion() {
		final String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1];
		return ServerVersionController.valueOf("V1_" + bukkitVersion);
	}
}
