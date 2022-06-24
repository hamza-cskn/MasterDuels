package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IMatchTeamManager {

	boolean isAllTeamsFull();

	List<IMember> getAllMembers();

	IMember getMember(UUID playerUniqueId);

	void unregisterMember(IMember member);

	IMember registerMember(Player player, IKit kit, int teamNo);

	void registerTeam(ITeam team);

	/**
	 * @return null, if 2 teams survived still.
	 */
	ITeam getLastSurvivedTeam();

	boolean checkTeamEliminated(final ITeam team);

	void unregisterTeam(int index);

	List<ITeam> getTeams();

	int getTeamAmount();

	void setTeamAmount(int teamAmount);

	int getTeamSize();

	void setTeamSize(int teamSize);

}
