package mc.obliviate.masterduels.api.events.spectator;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.events.arena.MatchEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreSpectatorJoinEvent extends Event implements MatchEvent, Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player spectator;
	private final IMatch game;
	private boolean isCancelled = false;

	public DuelMatchPreSpectatorJoinEvent(Player spectator, IMatch game) {
		this.spectator = spectator;
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getSpectator() {
		return spectator;
	}

	@Override
	public IMatch getMatch() {
		return game;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;
	}
}
