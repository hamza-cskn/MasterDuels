package mc.obliviate.masterduels.bossbar;

import com.hakan.core.HCore;
import com.hakan.core.message.bossbar.HBarColor;
import com.hakan.core.message.bossbar.HBarStyle;
import com.hakan.core.message.bossbar.HBossBar;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static mc.obliviate.masterduels.bossbar.BossBarHandler.CLOSING_TEXT_FORMAT;
import static mc.obliviate.masterduels.bossbar.BossBarHandler.NORMAL_TEXT_FORMAT;

public class InternalBossBarManager implements Listener {

	private final Map<Match, HBossBar> bossBarMap = new HashMap<>();

	public InternalBossBarManager(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
		if (event.getNewState().getMatchStateType().equals(MatchStateType.MATCH_STARING)) {
			HBossBar bossBar = HCore.createBossBar(NORMAL_TEXT_FORMAT, HBarColor.WHITE, HBarStyle.SEGMENTED_10);
			bossBarMap.put(event.getMatch(), bossBar);
			initializeBossBarTimer(event.getMatch(), bossBar);
			for (Member member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
				bossBar.addPlayer(member.getPlayer());
			}
		}
	}

	@EventHandler
	public void onDuelMatchLeave(DuelMatchMemberLeaveEvent event) {
		HBossBar bar = bossBarMap.get(event.getMatch());
		if (bar == null) return;
		bar.removePlayer(event.getMember().getPlayer());
	}

	private void initializeBossBarTimer(Match match, HBossBar bar) {
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
