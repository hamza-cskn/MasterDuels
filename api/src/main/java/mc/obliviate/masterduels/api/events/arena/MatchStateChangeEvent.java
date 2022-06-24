package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.MatchStateType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchStateChangeEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch game;
	private final MatchStateType old;

	public MatchStateChangeEvent(IMatch game, MatchStateType old) {
		this.game = game;
		this.old = old;
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

	public MatchStateType getOldGameState() {
		return old;
	}
}
