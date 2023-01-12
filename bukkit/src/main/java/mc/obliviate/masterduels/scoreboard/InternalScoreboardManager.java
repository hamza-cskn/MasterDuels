package mc.obliviate.masterduels.scoreboard;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public final class InternalScoreboardManager implements Listener {

    public void init(MasterDuels plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
        switch (event.getNewState().getMatchStateType()) {
            case PLAYING:
            case ROUND_STARTING:
            case ROUND_ENDING:
            case MATCH_ENDING:
                break;
            default:
                return;
        }
        for (Member member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
            if (member.showScoreboard())
                setupScoreboard(member, event.getNewState().getMatchStateType());
        }
    }

    @EventHandler
    public void onDuelMatchLeave(DuelMatchMemberLeaveEvent event) {
        uninstallScoreboard(event.getMember().getPlayer());
    }

    private void uninstallScoreboard(Player player) {
        InternalScoreboard.deleteIfPresent(player.getUniqueId());
    }

    public void setupScoreboard(Member member, MatchStateType type) {
        final ScoreboardFormatConfig formatConfig = ScoreboardConfig.getDefaultConfig().getFormatConfig(type);
        final Match match = member.getMatch();

        InternalScoreboard scoreboard = new InternalScoreboard(member.getPlayer().getUniqueId());
        scoreboard.setTitle(formatConfig.getTitle());
        scoreboard.setUpdateInterval(ScoreboardConfig.getDefaultConfig().getIntervalInTicks());
        scoreboard.update(sb -> {
            int lineNo = 0;
            List<String> list = formatConfig.getLines();
            for (String configLine : list) {
                String line = configLine;

                if (line.equalsIgnoreCase("{+opponents}")) {
                    for (final Member loopMember : match.getAllMembers()) {
                        if (member.getTeam().equals(loopMember.getTeam())) continue;

                        String[] minorLines = line.split("\\{newline}");

                        for (String minorLine : minorLines) {
                            if (!loopMember.getPlayer().isOnline()) {
                                minorLine = formatConfig.getQuitOpponentFormat();
                            } else if (UserHandler.isSpectator(loopMember.getPlayer().getUniqueId())) {
                                minorLine = formatConfig.getDeadOpponentFormat();
                            } else {
                                minorLine = formatConfig.getLiveOpponentFormat().replace("{health}", MessageUtils.getFirstDigits(loopMember.getPlayer().getHealth(), 2) + "");
                            }

                            minorLine = minorLine
                                    .replace("{ping}", "unknown")
                                    .replace("{name}", Utils.getDisplayName(loopMember.getPlayer()) + "");
                            sb.setLine(lineNo++, minorLine);
                        }
                    }
                } else {
                    line = line.replace("{round}", match.getGameDataStorage().getGameRoundData().getCurrentRound() + "");
                    line = line.replace("{map}", match.getArena().getMapName());
                    line = line.replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime()));
                    line = line.replace("{team-size}", member.getTeam().getMembers().size() + "");
                    sb.setLine(lineNo++, line);
                }
            }
        });
        scoreboard.show();
    }
}
