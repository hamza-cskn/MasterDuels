package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.game.spectator.SpectatorStorage;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MatchTeamManager {

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

	public Member registerMember(Player player, Kit kit, int teamNo) {
		final Team team = teams.get(teamNo);

		if (team == null)
			throw new IllegalStateException("member could not registered because team " + teamNo + " not found");
		if (team.getMembers().size() >= teamSize) throw new IllegalStateException("this team is full");

		final Member member = new Member(player, team, kit);
		team.registerMember(member);
		return member;
	}

	public void registerTeam(Team team) {
		if (teams.size() < teamAmount) {
			teams.add(team);
		} else {
			throw new IllegalStateException("all teams already created");
		}
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

	public void unregisterTeam(int index) {
		teams.remove(index);
	}

	public List<Team> getTeams() {
		return Collections.unmodifiableList(teams);
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public void setTeamAmount(int teamAmount) {
		this.teamAmount = teamAmount;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}
}
