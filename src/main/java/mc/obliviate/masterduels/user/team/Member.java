package mc.obliviate.masterduels.user.team;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.IUser;
import org.bukkit.entity.Player;

public class Member implements IMember, IUser {

	private final Team team;
	private final Player player;

	public Member(final Team team, final Player player) {
		this.team = team;
		this.player = player;
		DataHandler.getUsers().put(player.getUniqueId(), this);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	@Override
	public Game getGame() {
		return team.getGame();
	}
}
