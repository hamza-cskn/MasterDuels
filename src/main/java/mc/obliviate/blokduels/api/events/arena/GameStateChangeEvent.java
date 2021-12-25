package mc.obliviate.blokduels.api.events.arena;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Game game;
	private final GameState old;

	public GameStateChangeEvent(Game game, GameState old) {
		this.game = game;
		this.old = old;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public GameState getOldGameState() {
		return old;
	}

}
