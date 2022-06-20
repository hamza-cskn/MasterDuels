package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.GameState;
import mc.obliviate.masterduels.api.arena.IGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IGame game;
	private final GameState old;

	public GameStateChangeEvent(IGame game, GameState old) {
		this.game = game;
		this.old = old;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IGame getGame() {
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
