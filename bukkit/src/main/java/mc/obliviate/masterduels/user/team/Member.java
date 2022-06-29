package mc.obliviate.masterduels.user.team;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.team.Team;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import org.bukkit.entity.Player;

public class Member implements IUser {

	private final Player player;
	private final Team team;
	private final Kit kit;

	public Member(final Player player, final Team team, Kit kit) {
		this.team = team;
		this.player = player;
		this.kit = kit;
		DataHandler.getUsers().put(player.getUniqueId(), this);
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
