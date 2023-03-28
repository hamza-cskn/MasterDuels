package mc.obliviate.masterduels.bossbar;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.MasterDuels;

public final class BossBarHandler {

	private static BossBarConfig defaultBossBarConfig;
	private static BossBarModule bossBarModule;

	public void init(MasterDuels plugin) {
		Preconditions.checkNotNull(bossBarModule);
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

	public static void setDefaultConfig(BossBarConfig bossBarConfig) {
		BossBarHandler.defaultBossBarConfig = bossBarConfig;
	}

	public static void setBossBarModule(BossBarModule bossBarModule) {
		BossBarHandler.bossBarModule = bossBarModule;
	}

	public static BossBarConfig getDefaultConfig() {
		return defaultBossBarConfig;
	}

	public static BossBarModule getBossBarModule() {
		return bossBarModule;
	}
}
