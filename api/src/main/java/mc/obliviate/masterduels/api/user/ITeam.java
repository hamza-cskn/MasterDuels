package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IMatch;

import java.util.List;

public interface ITeam {

	IMatch getMatch();

	int getSize();

	List<IMember> getMembers();

	void unregisterMember(IMember member);

	void registerMember(IMember member);

	int getTeamId();

}
