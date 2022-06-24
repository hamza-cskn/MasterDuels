package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import org.bukkit.entity.Player;

public class DuelUser implements IUser {

	private final Player player;
	private final DuelStatistic statistic;

	public DuelUser(Player player, DuelStatistic statistic) {
		this.player = player;
		this.statistic = statistic;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public DuelStatistic getStatistic() {
		return statistic;
	}
}
