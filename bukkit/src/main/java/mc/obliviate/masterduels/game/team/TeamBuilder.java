package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.game.Match;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TeamBuilder {

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

	public Team build(Match game) {
		return new Team(teamId, getSize(), members, game);
	}

}
