package mc.obliviate.blokduels.bukkit.game;

import mc.obliviate.blokduels.bukkit.team.Team;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamBuilder {

	private final int teamId;
	private final int size;
	private final List<Player> members;

	public TeamBuilder(final int teamId, final int size, final List<Player> members) {
		this.teamId = teamId;
		this.size = size;
		this.members = members;
	}

	public int getSize() {
		return size;
	}

	public List<Player> getMembers() {
		return members;
	}

	public int getTeamId() {
		return teamId;
	}

	public Team build(Game game) {
		return new Team(teamId, size, members, game);
	}

}
