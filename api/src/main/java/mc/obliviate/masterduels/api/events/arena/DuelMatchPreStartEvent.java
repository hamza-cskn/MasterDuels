package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreStartEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch game;

	public DuelMatchPreStartEvent(IMatch game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IMatch getMatch() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
