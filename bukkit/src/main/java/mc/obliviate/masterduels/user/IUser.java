package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import org.bukkit.entity.Player;

public interface IUser {

	Player getPlayer();

	boolean isInMatchBuilder();

	MatchBuilder getMatchBuilder();

	void exitMatchBuilder();

	void setMatchBuilder(MatchBuilder duelSpace);

	boolean inviteReceiving();

	void setInviteReceiving(boolean inviteReceiving);

	DuelStatistic getStatistic();


}
