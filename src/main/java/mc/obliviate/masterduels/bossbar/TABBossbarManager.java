package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameState;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TABBossbarManager implements BossBarManager {

	public static String NORMAL_TEXT_FORMAT = "{time}";
	public static String CLOSING_TEXT_FORMAT = "{time}";

	private final BossBar bar;
	private final Game game;

	public TABBossbarManager(Game game) {
		this.game = game;
		if (!TABManager.isEnabled()) {
			this.bar = null;
			return;
		}
		final String title = NORMAL_TEXT_FORMAT.replace("{timer}", "...").replace("{time}", "...");
		//this.bar = TabAPI.getInstance().getBossBarManager().createBossBar("bossbar", NORMAL_TEXT_FORMAT.replace("{timer}", "...").replace("{time}", "..."), 100f, BarColor.WHITE, BarStyle.NOTCHED_10);
		this.bar = TabAPI.getInstance().getBossBarManager().createBossBar(title, 100f, BarColor.WHITE, BarStyle.NOTCHED_10);
	}

	@Override
	public void show(Member member) {
		if (this.bar == null) return;
		final TabPlayer player = TabAPI.getInstance().getPlayer(member.getPlayer().getUniqueId());
		bar.addPlayer(player);
	}

	@Override
	public void init() {
		if (this.bar == null) return;
		game.task("BOSSBAR", Bukkit.getScheduler().runTaskTimer(game.getPlugin(), () -> {
			if (game.getGameState().equals(GameState.GAME_ENDING)) {
				bar.setProgress((Utils.getPercentage(Game.getEndDelay() * 1000, (game.getTimer() - System.currentTimeMillis()))));
				bar.setTitle(CLOSING_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimerFormat(game.getTimer())).replace("{timer}", TimerUtils.formatTimerFormat(game.getTimer())));
			} else {
				bar.setProgress((Utils.getPercentage(game.getFinishTime() * 1000, (game.getTimer() - System.currentTimeMillis()))));
				bar.setTitle(NORMAL_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimerFormat(game.getTimer())).replace("{timer}", TimerUtils.formatTimerFormat(game.getTimer())));
			}
		}, 0, 20));
	}

	@Override
	public void finish() {
		for (final TabPlayer tabPlayer : bar.getPlayers()) {
			bar.removePlayer(tabPlayer);
		}
	}


}
