package mc.obliviate.masterduels.api.events.spectator;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.events.arena.GameEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameSpectatorLeaveEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final ISpectator spectator;

	public DuelGameSpectatorLeaveEvent(ISpectator spectator) {
		this.spectator = spectator;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IGame getGame() {
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