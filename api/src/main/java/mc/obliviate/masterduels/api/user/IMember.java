package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.kit.IKit;

public interface IMember extends IUser {

	ITeam getTeam();

	IMatch getMatch();

	IKit getKit();

}
