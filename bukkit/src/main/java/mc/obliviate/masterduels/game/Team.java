package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

	private final int teamId;
	private final int size;
	private final List<Member> members = new ArrayList<>();
	private final Match match;

	private Team(final int teamId, final int size, Match match, List<IUser> users) {
		this.teamId = teamId;
		this.size = size;
		this.match = match;
		for (IUser user : users) {
			members.add(UserHandler.switchMember(user, this, null));
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
		private final List<IUser> users = new ArrayList<>();

		Builder(int teamId, int size) {
			this.teamId = teamId;
			this.size = size;
		}

		protected void registerPlayer(Player player, Kit kit) {
			Preconditions.checkState(users.size() < size, "team is full");

			final IUser user = UserHandler.getUser(player.getUniqueId());
			if (user instanceof Member)
				throw new IllegalStateException("the player " + player.getName() + " is already a member at " + ((Member) user).getMatch().getArena().getName());
			if (user instanceof Spectator)
				throw new IllegalStateException("the player " + player.getName() + "is spectating.");

			users.add(user);
		}

		protected void unregisterPlayer(IUser user) {
			users.remove(user);
		}

		public int getSize() {
			return size;
		}

		public int getTeamId() {
			return teamId;
		}

		public List<IUser> getUsers() {
			return users;
		}

		public Team build(Match match) {
			return new Team(teamId, size, match, users);
		}


	}
}
