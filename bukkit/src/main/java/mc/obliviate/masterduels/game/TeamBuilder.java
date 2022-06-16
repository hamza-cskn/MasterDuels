package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.user.team.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamBuilder implements ITeamBuilder {

	private final int teamId;
	private final TeamBuilderManager teamBuilderManager;
	private final List<Player> members = new ArrayList<>();

	protected TeamBuilder(final TeamBuilderManager teamBuilderManager, final List<Player> members, int teamId) {
		this.teamBuilderManager = teamBuilderManager;
		this.teamId = teamId;
		if (members != null)
			this.members.addAll(members);
	}

	public int getSize() {
		return teamBuilderManager.getTeamSize();
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
		return new Team(teamId, getSize(), members, game);
	}

}
