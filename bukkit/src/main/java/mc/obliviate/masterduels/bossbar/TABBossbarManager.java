package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.api.arena.GameState;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import org.bukkit.Bukkit;

@Deprecated
public class TABBossbarManager implements BossBarManager {

	public static String NORMAL_TEXT_FORMAT = "{time}";
	public static String CLOSING_TEXT_FORMAT = "{time}";

	private final BossBar bar;
	private final Game game;

	public TABBossbarManager(final Game game) {
		this.game = game;
		if (!TABManager.isEnabled()) {
			this.bar = null;
			return;
		}
		final String title = NORMAL_TEXT_FORMAT.replace("{timer}", "...").replace("{time}", "...");
		this.bar = TabAPI.getInstance().getBossBarManager().createBossBar(title, 100f, BarColor.WHITE, BarStyle.NOTCHED_10);
	}

	@Override
	public void show(final IMember member) {
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
				bar.setTitle(CLOSING_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(game.getTimer())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(game.getTimer())));
			} else {
				bar.setProgress((Utils.getPercentage(game.getFinishTime() * 1000, (game.getTimer() - System.currentTimeMillis()))));
				bar.setTitle(NORMAL_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(game.getTimer())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(game.getTimer())));
			}
		}, 0, 20));
	}

	@Override
	public void finish() {
		for (final TabPlayer tabPlayer : bar.getPlayers()) {
			bar.removePlayer(tabPlayer);
		}
	}

	@Override
	public void hide(final IMember member) {
		final TabPlayer player = TabAPI.getInstance().getPlayer(member.getPlayer().getUniqueId());
		bar.removePlayer(player);

	}
}