package mc.obliviate.masterduels.user.spectator;

import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

/**
 * Purpose of this class
 * storing PURE SPECTATOR players.
 * <p>
 * PURE SPECTATORS
 * Spectator players from out of game,
 * not member.
 */
public class Spectator implements ISpectator {

	private final Game game;
	private final Player player;

	public Spectator(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
