package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

/**
 * Duels user interface
 * <p>
 * Spectators who watching duel game, players who is in duel game...
 */
public interface IUser {

	Game getGame();

	Player getPlayer();

}
