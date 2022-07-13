package mc.obliviate.masterduels.api.spectator;

import mc.obliviate.masterduels.api.arena.MatchEvent;
import mc.obliviate.masterduels.game.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreSpectatorJoinEvent extends Event implements MatchEvent, Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player spectator;
	private final Match match;
	private boolean isCancelled = false;

	public DuelMatchPreSpectatorJoinEvent(Player spectator, Match match) {
		this.spectator = spectator;
		this.match = match;
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
	public Match getMatch() {
		return match;
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
