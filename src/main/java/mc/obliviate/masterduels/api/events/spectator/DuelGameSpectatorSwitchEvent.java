package mc.obliviate.masterduels.api.events.spectator;

import mc.obliviate.masterduels.api.events.arena.GameEvent;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.spectator.Spectator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameSpectatorSwitchEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Spectator spectator;

	public DuelGameSpectatorSwitchEvent(Spectator spectator) {
		this.spectator = spectator;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Game getGame() {
		return spectator.getGame();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Spectator getSpectator() {
		return spectator;
	}
}
