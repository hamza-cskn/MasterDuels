package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.tab.TABManager;

public class BossBarHandler {

	public static BossBarManager getBossBarManager(Game game) {
		if (TABManager.isEnabled()) {
			return new TABBossbarManager(game);
		}
		return new DysfunctionalBossBarManager();
	}


}
