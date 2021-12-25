package mc.obliviate.blokduels.api.events.spectator;

import mc.obliviate.blokduels.api.events.arena.GameEvent;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.user.Spectator;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGamePreSpectatorJoinEvent extends Event implements GameEvent, Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player spectator;
	private final Game game;
	private boolean isCancelled = false;

	public DuelGamePreSpectatorJoinEvent(Player spectator, Game game) {
		this.spectator = spectator;
		this.game = game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getSpectator() {
		return spectator;
	}

	@Override
	public Game getGame() {
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
