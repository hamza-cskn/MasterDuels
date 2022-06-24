package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.IMatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchStateChangeEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch game;
	private final IMatchState oldState;
	private final IMatchState newState;

	public MatchStateChangeEvent(IMatch game, IMatchState oldState, IMatchState newState) {
		this.game = game;
		this.oldState = oldState;
		this.newState = newState;
	}

	public IMatchState getNewState() {
		return newState;
	}

	public IMatchState getOldState() {
		return oldState;
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
