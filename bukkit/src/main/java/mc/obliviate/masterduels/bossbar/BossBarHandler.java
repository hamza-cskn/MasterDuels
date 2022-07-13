package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.MasterDuels;

public final class BossBarHandler {

	public static String NORMAL_TEXT_FORMAT = "{time}";
	public static String CLOSING_TEXT_FORMAT = "{time}";
	private static BossBarModule bossBarModule;

	public void init(MasterDuels plugin) {
		switch (bossBarModule) {
			case TAB:
				new TABBossBarManager(plugin);
			case INTERNAL:
				new InternalBossBarManager(plugin);
		}
	}

	public enum BossBarModule {
		TAB,
		INTERNAL,
		DISABLED

	}

	public static void setBossBarModule(BossBarModule bossBarModule) {
		BossBarHandler.bossBarModule = bossBarModule;
	}

	public static BossBarModule getBossBarModule() {
		return bossBarModule;
	}
}
