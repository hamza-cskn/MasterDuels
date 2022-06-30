package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.spectator.SpectatorStorage;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * this object uses locked field as lock
 * when locked field is not null, match team manager is locked.
 **/
public class MatchTeamManager {

	private boolean locked = false;
	// when locked, team builders is dysfunctional
	private final List<Team.Builder> teamBuilders = new ArrayList<>();
	// when unlocked, team is dysfunctional
	private final List<Team> teams = new ArrayList<>();
	private int teamAmount = 2;
	private int teamSize = 1;

	public boolean isAllTeamsFull() {
		if (teams.size() != teamAmount) return false;

		for (Team team : teams) {
			if (team.getMembers().size() != teamSize) {
				return false;
			}
		}
		return true;
	}

	public List<Member> getAllMembers() {
		final List<Member> members = new ArrayList<>();
		for (final Team team : teams) {
			members.addAll(team.getMembers());
		}
		return members;
	}

	public Member getMember(UUID playerUniqueId) {
		for (Team team : teams) {
			for (Member member : team.getMembers()) {
				if (member.getPlayer().getUniqueId().equals(playerUniqueId)) {
					return member;
				}
			}
		}
		return null;
	}

	public boolean isMember(UUID playerUniqueId) {
		return getMember(playerUniqueId) != null;
	}

	public void unregisterMember(Member member) {
		member.getTeam().unregisterMember(member);
	}

	public void unregisterPlayer(Player player) {
		unregisterPlayer(UserHandler.getUser(player.getUniqueId()));
	}

	public void unregisterPlayer(IUser user) {
		for (Team.Builder builder : teamBuilders) {
			if (builder.getUsers().contains(user)) {
				builder.unregisterPlayer(user);
			}
		}
	}

	public void registerPlayer(Player player, Kit kit, int teamNo) {
		final Team.Builder teamBuilder = teamBuilders.get(teamNo - 1);
		if (teamBuilder == null)
			throw new IllegalStateException("member could not registered because team builder " + teamNo + " is not found");

		teamBuilder.registerPlayer(player, kit);
	}

	public void unregisterAllTeams() {
		Preconditions.checkState(!isLocked(), "this object is locked");
		teamBuilders.clear();
	}

	public void createAllTeams() {
		unregisterAllTeams();
		int safe = 0;
		while (teamBuilders.size() < teamAmount) {
			if (safe++ > 20) {
				throw new IllegalStateException(safe + " teams created. probably team create task repeated infinitely");
			}
			createNewTeam();
		}
	}

	private void createNewTeam() {
		Preconditions.checkState(!isLocked(), "this object is locked");
		if (teamBuilders.size() < teamAmount) {
			teamBuilders.add(new Team.Builder(teamBuilders.size() + 1, teamSize));
			Bukkit.broadcastMessage("team registered: " + teamBuilders.size());
		} else {
			throw new IllegalStateException("team amount limit is " + getTeamAmount() + ". all teams has created already. team amount is " + teams.size());
		}
	}

	public Team getTeam(Player player) {
		for (final Team team : teams) {
			for (Member member : team.getMembers()) {
				if (member.getPlayer().equals(player)) return team;
			}
		}
		return null;
	}

	public Team.Builder getTeamBuilder(Player player) {
		for (final Team.Builder teamBuilder : teamBuilders) {
			for (IUser user : teamBuilder.getUsers()) {
				if (user.getPlayer().equals(player)) return teamBuilder;
			}
		}
		return null;
	}

	/**
	 * @return null, if 2 teams survived still.
	 */
	public Team getLastSurvivedTeam() {
		Team survivedTeam = null;
		for (Team team : teams) {
			if (!checkTeamEliminated(team)) {
				if (survivedTeam != null) return null;
				survivedTeam = team;
			}
		}
		return survivedTeam;

	}

	public boolean checkTeamEliminated(final Team team) {
		final SpectatorStorage omniSpectatorStorage = team.getMatch().getGameSpectatorManager().getSemiSpectatorStorage();
		Loop1:
		for (Member member : team.getMembers()) {
			for (Spectator spectator : omniSpectatorStorage.getSpectatorList()) {
				if (member.getPlayer().equals(spectator.getPlayer())) {
					continue Loop1;
				}
			}
			return false;
		}
		return true;
	}

	public List<Team> getTeams() {
		return Collections.unmodifiableList(teams);
	}

	public List<Team.Builder> getTeamBuilders() {
		return Collections.unmodifiableList(teamBuilders);
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public void setTeamsAttributes(int size, int amount) {
		Preconditions.checkState(!isLocked(), "this object is locked");
		this.teamSize = size;
		this.teamAmount = amount;
		createAllTeams();
	}


	public int getTeamSize() {
		return teamSize;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock(Match match) {
		teamBuilders.forEach(builder -> teams.add(builder.build(match)));
		teamBuilders.clear();
		this.locked = true;
	}
}
