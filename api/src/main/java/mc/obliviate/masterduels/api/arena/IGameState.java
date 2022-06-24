package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import org.bukkit.entity.Player;

public interface IGameState {

	void next();

	void leave(IMember member);

	void leave(ISpectator spectator);

	void join(Player player, IKit kit, int teamNo);

	GameStateType getGameStateType();
}
