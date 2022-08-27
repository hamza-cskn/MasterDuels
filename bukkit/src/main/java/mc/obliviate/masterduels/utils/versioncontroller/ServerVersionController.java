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
	V1_16,
	V1_17,
	V1_18,
	V1_19,
	NEWER;

	private static mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController serverVersion;

	public static boolean isServerVersionAbove(mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController version) {
		return getServerVersion().ordinal() > version.ordinal();
	}

	public static boolean isServerVersionAtLeast(mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController version) {
		return getServerVersion().ordinal() >= version.ordinal();
	}

	public static boolean isServerVersionAtOrBelow(mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController version) {
		return getServerVersion().ordinal() <= version.ordinal();
	}

	public static boolean isServerVersionBelow(mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController version) {
		return getServerVersion().ordinal() < version.ordinal();
	}

	public static mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController getServerVersion() {
		if (serverVersion == null) serverVersion = calculateServerVersion();
		return serverVersion;
	}

	private static mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController calculateServerVersion() {
		final String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1];
		try {
			return mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController.valueOf("V1_" + bukkitVersion);
		} catch (Exception e) {
			return UNKNOWN;
		}
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
