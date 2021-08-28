package mc.obliviate.blokduels.team;

import mc.obliviate.blokduels.data.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Member {

	private final Team team;
	private final Player player;

	public Member(Team team, Player player) {
		this.team = team;
		this.player = player;
		Bukkit.broadcastMessage("members created");
		player.sendMessage("you're a member now.");
		DataHandler.getMembers().put(player.getUniqueId(), this);
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

}
