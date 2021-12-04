package mc.obliviate.blokduels.bukkit.team;

import mc.obliviate.blokduels.bukkit.data.DataHandler;
import org.bukkit.entity.Player;

public class Member {

	private final Team team;
	private final Player player;

	public Member(Team team, Player player) {
		this.team = team;
		this.player = player;
		DataHandler.getMembers().put(player.getUniqueId(), this);
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

}
