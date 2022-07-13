package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

	private final int teamId;
	private final int size;
	private final List<Member> members = new ArrayList<>();
	private final Match match;

	private Team(final int teamId, final int size, Match match, List<Member.Builder> users) {
		this.teamId = teamId;
		this.size = size;
		this.match = match;
		for (Member.Builder memberBuilder : users) {
			members.add(memberBuilder.buildAndSwitch(this));
		}
	}

	// called when rejoin
	protected Member registerMember(Player player, Kit kit) {
		Preconditions.checkState(members.size() < size, "team is full");

		final IUser user = UserHandler.getUser(player.getUniqueId());
		if (user instanceof Member)
			throw new IllegalStateException("the player " + player.getName() + " is already a member at " + ((Member) user).getMatch().getArena().getName());
		if (user instanceof Spectator)
			throw new IllegalStateException("the player " + player.getName() + "is spectating.");

		final Member member = UserHandler.switchMember(user, this, kit);
		members.add(member);
		return member;
	}

	protected void unregisterMember(Member member) {
		members.remove(member);
		member.exitMatchBuilder();
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

	public int getTeamId() {
		return teamId;
	}

	public static class Builder {

		private final int teamId;
		private final int size;
		private final List<Member.Builder> memberBuilders = new ArrayList<>();

		Builder(int teamId, int size) {
			this.teamId = teamId;
			this.size = size;
		}

		protected void registerPlayer(Player player, Kit kit) {
			Preconditions.checkState(memberBuilders.size() < size, "team is full");

			final IUser user = UserHandler.getUser(player.getUniqueId());
			if (user instanceof Member)
				throw new IllegalStateException("the player " + player.getName() + " is already a member at " + ((Member) user).getMatch().getArena().getName());
			if (user instanceof Spectator)
				throw new IllegalStateException("the player " + player.getName() + "is spectating.");

			memberBuilders.add(new Member.Builder((User) user, kit));
		}

		protected void unregisterPlayer(Member.Builder builder) {
			memberBuilders.remove(builder);
		}

		public int getSize() {
			return size;
		}

		public int getTeamId() {
			return teamId;
		}

		public List<Member.Builder> getMemberBuilders() {
			return memberBuilders;
		}

		public Team build(Match match) {
			return new Team(teamId, size, match, memberBuilders);
		}
	}
}
