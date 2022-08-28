package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.bossbar.BarColor;
import me.neznamy.tab.api.bossbar.BarStyle;
import me.neznamy.tab.api.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TABBossBarManager implements IBossBarManager, Listener {

	private final Map<Match, BossBar> bossBarMap = new HashMap<>();

	public TABBossBarManager(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
		if (event.getNewState().getMatchStateType().equals(MatchStateType.PLAYING)) {
			final BossBar bossBar = TabAPI.getInstance().getBossBarManager().createBossBar(BossBarHandler.getDefaultConfig().getPlayingTextFormat(), 100, BarColor.WHITE, BarStyle.NOTCHED_10);
			bossBarMap.put(event.getMatch(), bossBar);
			initializeBossBarTimer(event.getMatch(), bossBar);
			for (Member member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
				bossBar.addPlayer(TabAPI.getInstance().getPlayer(member.getPlayer().getUniqueId()));
			}
		}
	}

	@EventHandler
	public void onDuelMatchLeave(DuelMatchMemberLeaveEvent event) {
		final BossBar bar = bossBarMap.get(event.getMatch());
		if (bar == null) return;
		bar.removePlayer(TabAPI.getInstance().getPlayer(event.getMember().getPlayer().getUniqueId()));
	}

	private void initializeBossBarTimer(Match match, BossBar bar) {
		match.getGameTaskManager().repeatTask("BOSSBAR", () -> {
			if (match.getMatchState().getMatchStateType().equals(MatchStateType.MATCH_ENDING)) {
				bar.setProgress((Utils.getPercentage(MatchDataStorage.getEndDelay().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(BossBarHandler.getDefaultConfig().getEndingTextFormat().replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
			} else {
				bar.setProgress((Utils.getPercentage(match.getGameDataStorage().getMatchDuration().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(BossBarHandler.getDefaultConfig().getPlayingTextFormat().replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
			}
		}, null, 0, 20);
	}
}
