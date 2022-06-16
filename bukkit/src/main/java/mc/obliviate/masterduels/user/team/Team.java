package mc.obliviate.masterduels.user.team;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team implements ITeam {

	private final int teamId;
	private final int size;
	private final List<IMember> members = new ArrayList<>();
	private final Game game;

	public Team(final int teamId, final int size, final List<Player> members, final Game game) {
		this.teamId = teamId;
		this.size = size;
		this.game = game;

		members.forEach(p -> this.members.add(new Member(this, p)));
	}

	@Override
	public IGame getGame() {
		return game;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<IMember> getMembers() {
		return new ArrayList<>(members);
	}

	@Override
	public void removeMember(IMember member) {
		members.remove(member);
	}

	@Override
	public int getTeamId() {
		return teamId;
	}
}
