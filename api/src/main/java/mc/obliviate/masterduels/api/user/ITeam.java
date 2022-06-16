package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IGame;

import java.util.List;

public interface ITeam {

	IGame getGame();

	int getSize();

	List<IMember> getMembers();

	void removeMember(IMember member);

	int getTeamId();

}
