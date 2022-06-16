package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IGame;
import org.bukkit.entity.Player;

public interface ISpectator extends IUser {

	Player getPlayer();

	IGame getGame();

}
