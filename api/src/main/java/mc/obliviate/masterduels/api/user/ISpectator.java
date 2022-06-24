package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IMatch;
import org.bukkit.entity.Player;

public interface ISpectator extends IUser {

	Player getPlayer();

	IMatch getGame();

}
