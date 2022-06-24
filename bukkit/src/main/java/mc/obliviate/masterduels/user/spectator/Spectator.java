package mc.obliviate.masterduels.user.spectator;

import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Match;
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

	private final Match game;
	private final Player player;

	public Spectator(Match game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public Match getGame() {
		return game;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
