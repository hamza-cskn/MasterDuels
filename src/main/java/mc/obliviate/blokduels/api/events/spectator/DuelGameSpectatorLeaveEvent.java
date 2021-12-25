package mc.obliviate.blokduels.api.events.spectator;

import mc.obliviate.blokduels.api.events.arena.GameEvent;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.user.Spectator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameSpectatorLeaveEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Spectator spectator;

	public DuelGameSpectatorLeaveEvent(Spectator spectator) {
		this.spectator = spectator;
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
