package mc.obliviate.blokduels.user.team;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.user.User;
import org.bukkit.entity.Player;

public class Member implements User {

	private final Team team;
	private final Player player;

	public Member(Team team, Player player) {
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
