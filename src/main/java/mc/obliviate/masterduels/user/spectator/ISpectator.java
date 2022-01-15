package mc.obliviate.masterduels.user.spectator;

import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

public interface ISpectator {

	Player getPlayer();
	Game getGame();

}
