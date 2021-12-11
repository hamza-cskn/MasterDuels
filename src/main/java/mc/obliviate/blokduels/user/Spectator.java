package mc.obliviate.blokduels.user;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.kit.InventoryStorer;
import org.bukkit.entity.Player;

public class Spectator implements User {

	private final Game game;
	private final Player player;

	public Spectator(Game game, Player player, boolean saveInventory) {
		this.game = game;
		this.player = player;
		if (saveInventory) {
			InventoryStorer.store(player);
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
