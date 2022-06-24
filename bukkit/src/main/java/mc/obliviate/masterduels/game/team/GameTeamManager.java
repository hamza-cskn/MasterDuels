package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.api.arena.IGameTeamManager;
import mc.obliviate.masterduels.api.arena.spectator.ISpectatorStorage;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GameTeamManager implements IGameTeamManager {

	private final List<ITeam> teams = new ArrayList<>();
	private int teamAmount = 2;
	private int teamSize = 1;

	public boolean isAllTeamsFull() {
		if (teams.size() != teamAmount) return false;

		for (ITeam team : teams) {
			if (team.getMembers().size() != teamSize) {
				return false;
			}
		}
		return true;
	}

	public List<IMember> getAllMembers() {
		final List<IMember> members = new ArrayList<>();
		for (final ITeam team : teams) {
			members.addAll(team.getMembers());
		}
		return members;
	}

	public IMember getMember(UUID playerUniqueId) {
		for (ITeam team : teams) {
			for (IMember member : team.getMembers()) {
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

	public void unregisterMember(IMember member) {
		member.getTeam().unregisterMember(member);
	}

	public IMember registerMember(Player player, IKit kit, int teamNo) {
		final ITeam team = teams.get(teamNo);

		if (team == null)
			throw new IllegalStateException("member could not registered because team " + teamNo + " not found");
		if (team.getMembers().size() >= teamSize) throw new IllegalStateException("this team is full");

		final IMember member = new Member(player, team, kit);
		team.registerMember(member);
		return member;
	}

	public void registerTeam(ITeam team) {
		if (teams.size() < teamAmount) {
			teams.add(team);
		} else {
			throw new IllegalStateException("all teams already created");
		}
	}

	/**
	 * @return null, if 2 teams survived still.
	 */
	public ITeam getLastSurvivedTeam() {
		ITeam survivedTeam = null;
		for (ITeam team : teams) {
			if (!checkTeamEliminated(team)) {
				if (survivedTeam != null) return null;
				survivedTeam = team;
			}
		}
		return survivedTeam;

	}

	public boolean checkTeamEliminated(final ITeam team) {
		final ISpectatorStorage omniSpectatorStorage = team.getGame().getGameSpectatorManager().getOmniSpectatorStorage();
		Loop1:
		for (IMember member : team.getMembers()) {
			for (ISpectator spectator : omniSpectatorStorage.getSpectatorList()) {
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

	public List<ITeam> getTeams() {
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
