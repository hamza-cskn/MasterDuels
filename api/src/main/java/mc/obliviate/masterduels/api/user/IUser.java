package mc.obliviate.masterduels.api.user;

import mc.obliviate.masterduels.api.arena.IGame;
import org.bukkit.entity.Player;

/**
 * Duels user interface
 * <p>
 * Spectators who watching duel game, players who is in duel game...
 */
public interface IUser {

	IGame getGame();

	Player getPlayer();

}
