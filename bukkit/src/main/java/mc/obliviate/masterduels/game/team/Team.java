package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team implements ITeam {

	private final int teamId;
	private final int size;
	private final List<IMember> members = new ArrayList<>();
	private final IMatch match;

	protected Team(final int teamId, final int size, final List<Player> members, final IMatch match) {
		this.teamId = teamId;
		this.size = size;
		this.match = match;
		for (Player player : members) {
			this.members.add(new Member(player, this, null));
		}
	}

	@Override
	public IMatch getMatch() {
		return match;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<IMember> getMembers() {
		return Collections.unmodifiableList(members);
	}

	@Override
	public void unregisterMember(IMember member) {
		members.remove(member);
	}

	@Override
	public void registerMember(IMember member) {
		members.remove(member);
	}

	@Override
	public int getTeamId() {
		return teamId;
	}
}
