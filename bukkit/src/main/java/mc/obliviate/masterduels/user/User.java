package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User implements IUser {

    //player uuid, duel user
    private final Player player;
    private boolean inviteReceiving;
    private boolean showBossBar;
    private boolean showScoreboard;
    private final DuelStatistic statistic;
    private MatchBuilder matchBuilder = null;

    public User(Player player, boolean inviteReceiving, boolean showScoreboard, boolean showBossBar, DuelStatistic statistic) {
        this.player = player;
        this.inviteReceiving = inviteReceiving;
        this.showScoreboard = showScoreboard;
        this.showBossBar = showBossBar;
        this.statistic = statistic;
    }

    @Override
    public boolean isInMatchBuilder() {
        return matchBuilder != null;
    }

    @Override
    public MatchBuilder getMatchBuilder() {
        return matchBuilder;
    }

    @Override
    public void exitMatchBuilder() {
        setMatchBuilder(null);
    }

    @Override
    public void setMatchBuilder(MatchBuilder duelSpace) {
        this.matchBuilder = duelSpace;
    }

    @Override
    public boolean inviteReceiving() {
        return inviteReceiving;
    }

    @Override
    public void setInviteReceiving(boolean inviteReceiving) {
        this.inviteReceiving = inviteReceiving;
    }

    @Override
    public DuelStatistic getStatistic() {
        return statistic;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean showScoreboard() {
        return showScoreboard;
    }

    @Override
    public void setShowScoreboard(boolean showScoreboard) {
        this.showScoreboard = showScoreboard;
    }

    @Override
    public boolean showBossBar() {
        return showBossBar;
    }

    @Override
    public void setShowBossBar(boolean showBossBar) {
        this.showBossBar = showBossBar;
    }
}
