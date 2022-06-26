package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.IMatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchUninstallEvent extends Event implements MatchEvent {

	private final IMatch match;
	private final IMatchState state;
	private static final HandlerList handlers = new HandlerList();

	public DuelMatchUninstallEvent(IMatch match, IMatchState state) {
		this.match = match;
		this.state = state;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IMatchState getState() {
		return state;
	}

	@Override
	public IMatch getMatch() {
		return match;
	}
}