package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.game.Game;

public class BossBarHandler {

	public static String NORMAL_TEXT_FORMAT = "{time}";
	public static String CLOSING_TEXT_FORMAT = "{time}";
	private static BossBarModule bossBarModule;

	public static IBossBarManager getBossBarManager(Game game) {
		switch (bossBarModule) {
			case TAB:
				return new TABBossBarManager(game);
			case INTERNAL:
				return new InternalBossBarManager(game);
			default:
				return new DysfunctionalBossBarManager();
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
