package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.spectator.SpectatorManager;
import org.bukkit.entity.Player;

public class Spectator implements User {

	private final Game game;
	private final Player player;

	public Spectator(Game game, Player player, boolean saveInventory) {
		this.game = game;
		this.player = player;
		if (saveInventory) {
			SpectatorManager.giveSpectatorItems(player);
		}
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
