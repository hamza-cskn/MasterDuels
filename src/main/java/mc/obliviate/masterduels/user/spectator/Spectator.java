package mc.obliviate.masterduels.user.spectator;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.spectator.SpectatorHandler;
import mc.obliviate.masterduels.user.IUser;
import org.bukkit.entity.Player;

/**
 * Purpose of this class
 * storing PURE SPECTATOR players.
 *
 * PURE SPECTATORS
 * Spectator players from out of game,
 * not member.
 */
public class Spectator implements ISpectator, IUser {

	private final Game game;
	private final Player player;

	public Spectator(Game game, Player player) {
		this.game = game;
		this.player = player;
		SpectatorHandler.giveSpectatorItems(player);
		DataHandler.getUsers().put(player.getUniqueId(), this);
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
