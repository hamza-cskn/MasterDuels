package mc.obliviate.blokduels.bossbar;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.utils.tab.TABManager;

public class BossBarHandler {

	public static BossBarManager getBossBarManager(Game game) {
		if (TABManager.isEnabled()) {
			return new TABBossbarManager(game);
		}
		return new DysfunctionalBossBarManager();
	}


}
