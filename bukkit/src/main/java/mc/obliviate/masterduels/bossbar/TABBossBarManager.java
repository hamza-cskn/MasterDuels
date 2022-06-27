package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.events.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.events.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.MatchDataStorage;
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

import static mc.obliviate.masterduels.bossbar.BossBarHandler.CLOSING_TEXT_FORMAT;
import static mc.obliviate.masterduels.bossbar.BossBarHandler.NORMAL_TEXT_FORMAT;

public class TABBossBarManager implements IBossBarManager, Listener {

	private final Map<IMatch, BossBar> bossBarMap = new HashMap<>();

	public TABBossBarManager(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
		if (event.getNewState().getMatchStateType().equals(MatchStateType.PLAYING)) {
			final BossBar bossBar = TabAPI.getInstance().getBossBarManager().createBossBar(NORMAL_TEXT_FORMAT, 100, BarColor.WHITE, BarStyle.NOTCHED_10);
			bossBarMap.put(event.getMatch(), bossBar);
			initializeBossBarTimer(event.getMatch(), bossBar);
			for (IMember member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
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

	private void initializeBossBarTimer(IMatch match, BossBar bar) {
		match.getGameTaskManager().repeatTask("BOSSBAR", () -> {
			if (match.getMatchState().getMatchStateType().equals(MatchStateType.MATCH_ENDING)) {
				bar.setProgress((Utils.getPercentage(MatchDataStorage.getEndDelay().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(CLOSING_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
			} else {
				bar.setProgress((Utils.getPercentage(match.getGameDataStorage().getMatchDuration().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis()))));
				bar.setTitle(NORMAL_TEXT_FORMAT.replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
			}
		}, null, 0, 20);
	}
}
