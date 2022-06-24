package mc.obliviate.masterduels.api.events.spectator;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.events.arena.MatchEvent;
import mc.obliviate.masterduels.api.user.ISpectator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchSpectatorSwitchEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final ISpectator spectator;

	public DuelMatchSpectatorSwitchEvent(ISpectator spectator) {
		this.spectator = spectator;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public IMatch getMatch() {
		return spectator.getGame();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public ISpectator getSpectator() {
		return spectator;
	}
}
