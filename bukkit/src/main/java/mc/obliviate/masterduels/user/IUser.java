package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import org.bukkit.entity.Player;

public interface IUser {

    Player getPlayer();

    boolean isInMatchBuilder();

    MatchBuilder getMatchBuilder();

    void exitMatchBuilder();

    void setMatchBuilder(MatchBuilder duelSpace);

    boolean inviteReceiving();

    boolean showBossBar();

    void setShowBossBar(boolean showBossBar);

    boolean showScoreboard();

    void setShowScoreboard(boolean showScoreboard);

    void setInviteReceiving(boolean inviteReceiving);

    DuelStatistic getStatistic();


}
