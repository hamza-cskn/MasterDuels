package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.user.team.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamBuilder {

	private final int teamId;
	private final int size;
	private final List<Player> members = new ArrayList<>();

	public TeamBuilder(final int teamId, final int size, final List<Player> members) {
		this.teamId = teamId;
		this.size = size;
		if (members != null)
			this.members.addAll(members);
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

	public void add(Player p) {
		members.add(p);
	}

	public void remove(Player p) {
		members.remove(p);
	}
	public Team build(Game game) {
		return new Team(teamId, size, members, game);
	}

}
