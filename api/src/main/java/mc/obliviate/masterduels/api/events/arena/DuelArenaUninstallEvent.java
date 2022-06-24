package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelArenaUninstallEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch match;

	public DuelArenaUninstallEvent(IMatch match) {
		this.match = match;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IMatch getMatch() {
		return match;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
