package mc.obliviate.blokduels.user;

import mc.obliviate.blokduels.game.Game;
import org.bukkit.entity.Player;

/**
 * Duels user interface
 * <p>
 * Spectators who watching duel game, players who is in duel game...
 */
public interface User {

	Game getGame();

	Player getPlayer();

}
