package mc.obliviate.blokduels.game.bossbar;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.tab.TABManager;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import me.neznamy.tab.api.TABAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import org.bukkit.Bukkit;

public class BossBarData {

	private final BossBar bar;
	private final Game game;

	public BossBarData(Game game) {
		this.game = game;
		if (!TABManager.isEnabled()) {
			this.bar = null;
			return;
		}
		this.bar = TABAPI.createBossBar("bossbar", "Kalan Süre: ...", 100f, BarColor.WHITE, BarStyle.NOTCHED_10);
	}

	public void show(Member member) {
		if (this.bar == null) return;
		final TabPlayer player = TABAPI.getPlayer(member.getPlayer().getUniqueId());
		player.showBossBar(bar);
	}

	public void init() {
		if (this.bar == null) return;
		game.task("BOSSBAR", Bukkit.getScheduler().runTaskTimer(game.getPlugin(), () -> {
			bar.setTitle("Kalan Süre: " + TimerUtils.convertTimer(game.getTimer()));
			bar.setProgress(((float) (game.getTimer() - System.currentTimeMillis())));
		}, 0, 20));
	}

}
