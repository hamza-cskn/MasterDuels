package mc.obliviate.masterduels.bossbar;

import com.hakan.core.HCore;
import com.hakan.core.message.bossbar.HBarColor;
import com.hakan.core.message.bossbar.HBarStyle;
import com.hakan.core.message.bossbar.HBossBar;
import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameDataStorage;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.entity.Player;

import static mc.obliviate.masterduels.bossbar.BossBarHandler.CLOSING_TEXT_FORMAT;
import static mc.obliviate.masterduels.bossbar.BossBarHandler.NORMAL_TEXT_FORMAT;

public class InternalBossBarManager implements IBossBarManager {

	private final HBossBar bar;
	private final Game game;

	public InternalBossBarManager(final Game game) {
		this.game = game;

		final String title = NORMAL_TEXT_FORMAT.replace("{timer}", "...").replace("{time}", "...");
		this.bar = HCore.createBossBar(title, HBarColor.WHITE, HBarStyle.SEGMENTED_10);
	}

	@Override
	public void show(final IMember member) {
		if (this.bar == null) {
			Logger.debug(Logger.DebugPart.GAME, "Bar could not show to " + member.getPlayer().getName() + " because it was null.");
			return;
		}
		bar.addPlayer(member.getPlayer());
	}

	@Override
	public void init() {
		if (this.bar == null) {
			Logger.debug(Logger.DebugPart.GAME, "Internal boss bar manager could not inited because bar is null");
			return;
		}

		game.getGameTaskManager().repeatTask("BOSSBAR", () -> {
			if (game.getGameState().getGameStateType().equals(GameStateType.GAME_ENDING)) {
				bar.setProgress((Utils.getPercentage(GameDataStorage.getEndDelay().toMillis(), (game.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(CLOSING_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(game.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(game.getGameDataStorage().getFinishTime())));
			} else {
				bar.setProgress((Utils.getPercentage(game.getGameDataStorage().getMatchDuration().toMillis(), (game.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(NORMAL_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(game.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(game.getGameDataStorage().getFinishTime())));
			}
		}, this::finish, 0, 20);
	}

	@Override
	public void finish() {
		for (final Player player : bar.getPlayers()) {
			bar.removePlayer(player);
		}
	}

	@Override
	public void hide(final IMember member) {
		bar.removePlayer(member.getPlayer());
	}
}
