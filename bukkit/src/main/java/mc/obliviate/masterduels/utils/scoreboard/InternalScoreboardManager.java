package mc.obliviate.masterduels.utils.scoreboard;

import com.hakan.core.HCore;
import com.hakan.core.scoreboard.HScoreboard;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.events.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.events.arena.MatchStateChangeEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InternalScoreboardManager implements Listener {

	public InternalScoreboardManager(MasterDuels plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onDuelMatchStateChange(MatchStateChangeEvent event) {
		if (event.getMatch().getMatchState().getMatchStateType().equals(MatchStateType.UNINSTALLING)) return;
		for (IMember member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
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

	public void setupScoreboard(IMember member, MatchStateType type) {
		uninstallScoreboard(member.getPlayer());
		final HScoreboard scoreboard = HCore.createScoreboard(member.getPlayer());

		final ScoreboardFormatConfig formatConfig = ScoreboardFormatConfig.getFormatConfig(type);

		scoreboard.setTitle(formatConfig.getTitle());
		scoreboard.setUpdateInterval(20);
		scoreboard.show();

		final IMatch match = member.getMatch();
		scoreboard.update(hScoreboard -> {
			int lineNo = 0;
			for (String line : formatConfig.getLines()) {

				if (line.equalsIgnoreCase("{+opponents}")) {

					for (final IMember loopMember : match.getAllMembers()) {
						if (member.getTeam().equals(loopMember.getTeam())) continue;

						if (!loopMember.getPlayer().isOnline()) {
							line = formatConfig.getQuitOpponentFormat();
						} else if (match.getGameSpectatorManager().isSpectator(loopMember.getPlayer())) {
							line = formatConfig.getDeadOpponentFormat();
						} else {
							line = formatConfig.getLiveOpponentFormat().replace("{health}", loopMember.getPlayer().getHealthScale() + "");
						}

						line = line.replace("{name}", Utils.getDisplayName(loopMember.getPlayer()) + "");
						scoreboard.setLine(lineNo++, trimString(line, 16));
					}
				} else {
					line = line.replace("{round}", "" + match.getGameDataStorage().getGameRoundData().getCurrentRound())
							.replace("{map}", match.getArena().getMapName() + "")
							.replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime()))
							.replace("{team-size}", member.getTeam().getMembers().size() + "");
					scoreboard.setLine(lineNo++, trimString(line, 16));
				}
			}
		});
	}

	private static String trimString(String string, int charLimit) {
		if (string.length() > charLimit) {
			return string.substring(0, charLimit);
		}
		return string;
	}


}
