package mc.obliviate.masterduels.scoreboard;

import com.hakan.core.HCore;
import com.hakan.core.scoreboard.HScoreboard;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;

public final class InternalScoreboardManager implements Listener {

	public void init(MasterDuels plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
		if (event.getNewState().getMatchStateType().equals(MatchStateType.UNINSTALLING)) return;
		if (event.getNewState().getMatchStateType().equals(MatchStateType.MATCH_STARING)) return;
		if (event.getNewState().getMatchStateType().equals(MatchStateType.IDLE)) return;
		for (Member member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
			setupScoreboard(member, event.getNewState().getMatchStateType());
		}
	}

	@EventHandler
	public void onDuelMatchLeave(DuelMatchMemberLeaveEvent event) {
		uninstallScoreboard(event.getMember().getPlayer());
	}

	private void uninstallScoreboard(Player player) {
		HCore.findScoreboardByPlayer(player).ifPresent(HScoreboard::delete);
	}

	public void setupScoreboard(Member member, MatchStateType type) {
		uninstallScoreboard(member.getPlayer());
		HCore.scheduler(false).after(Duration.ofMillis(1050)).run(() -> {
			final ScoreboardFormatConfig formatConfig = ScoreboardFormatConfig.getFormatConfig(type);
			final Match match = member.getMatch();

			HScoreboard scoreboard = HCore.createScoreboard(member.getPlayer(), formatConfig.getTitle());
			scoreboard.update(20, hScoreboard -> {
				int lineNo = 0;
				for (String line : formatConfig.getLines()) {
					if (line.equalsIgnoreCase("{+opponents}")) {
						for (final Member loopMember : match.getAllMembers()) {
							if (member.getTeam().equals(loopMember.getTeam())) continue;

							if (!loopMember.getPlayer().isOnline()) {
								line = formatConfig.getQuitOpponentFormat();
							} else if (UserHandler.isSpectator(loopMember.getPlayer().getUniqueId())) {
								line = formatConfig.getDeadOpponentFormat();
							} else {
								line = formatConfig.getLiveOpponentFormat().replace("{health}", loopMember.getPlayer().getHealthScale() + "");
							}

							line = line.replace("{name}", Utils.getDisplayName(loopMember.getPlayer()) + "");
							scoreboard.setLine(lineNo++, line);
						}
					} else {
						line = line.replace("{round}", match.getGameDataStorage().getGameRoundData().getCurrentRound() + "")
								.replace("{map}", match.getArena().getMapName())
								.replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime()))
								.replace("{team-size}", member.getTeam().getMembers().size() + "");
						scoreboard.setLine(lineNo++, line);
					}
				}
			});
		});
	}
}
