package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.kit.IKit;

public interface IMember extends IUser {

	ITeam getTeam();

	IGame getGame();

	IKit getKit();

}
