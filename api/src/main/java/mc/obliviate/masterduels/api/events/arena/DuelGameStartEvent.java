package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameStartEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IGame game;

	public DuelGameStartEvent(IGame game) {
		this.game = game;
	}

	public IGame getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
