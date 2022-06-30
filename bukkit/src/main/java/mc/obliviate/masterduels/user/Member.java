package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import org.bukkit.entity.Player;

public class Member extends User implements IUser {

	private final Player player;
	private final Team team;
	private final Kit kit;

	Member(final Player player, final Team team, Kit kit, boolean inviteReceiving, DuelStatistic statistic) {
		super(player, inviteReceiving, statistic);
		this.team = team;
		this.player = player;
		this.kit = kit;
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	public Match getMatch() {
		return team.getMatch();
	}

	public Kit getKit() {
		return kit;
	}

}
