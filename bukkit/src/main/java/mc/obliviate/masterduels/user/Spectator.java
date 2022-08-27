package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import org.bukkit.entity.Player;

/**
 * Purpose of this class
 * storing PURE SPECTATOR players.
 * <p>
 * PURE SPECTATORS
 * Spectator players from out of game,
 * not member.
 */
public class Spectator extends User {

    private final Match match;
    private final Player player;

    Spectator(Player player, Match match, boolean inviteReceiving, boolean showScoreboard, boolean showBossBar, DuelStatistic statistic) {
        super(player, inviteReceiving, showScoreboard, showBossBar, statistic);
        this.match = match;
        this.player = player;
    }

    public Match getMatch() {
        return match;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void exitMatchBuilder() {
        super.exitMatchBuilder();
        UserHandler.switchUser(this);
    }
}
