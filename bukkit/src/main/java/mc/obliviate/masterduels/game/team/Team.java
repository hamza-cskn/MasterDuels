package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

	private final int teamId;
	private final int size;
	private final List<Member> members = new ArrayList<>();
	private final Match match;

	protected Team(final int teamId, final int size, final List<Player> members, final Match match) {
		this.teamId = teamId;
		this.size = size;
		this.match = match;
		for (Player player : members) {
			this.members.add(new Member(player, this, null));
		}
	}

	public Match getMatch() {
		return match;
	}

	public int getSize() {
		return size;
	}

	public List<Member> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public void unregisterMember(Member member) {
		members.remove(member);
	}

	public void registerMember(Member member) {
		members.remove(member);
	}

	public int getTeamId() {
		return teamId;
	}
}
