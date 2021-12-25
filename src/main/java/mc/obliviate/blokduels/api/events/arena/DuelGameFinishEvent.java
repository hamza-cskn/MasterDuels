package mc.obliviate.blokduels.api.events.arena;

import mc.obliviate.blokduels.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameFinishEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Game game;

	public DuelGameFinishEvent(Game game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
