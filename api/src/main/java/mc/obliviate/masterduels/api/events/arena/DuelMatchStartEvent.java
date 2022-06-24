package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchStartEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch game;

	public DuelMatchStartEvent(IMatch game) {
		this.game = game;
	}

	public IMatch getMatch() {
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
