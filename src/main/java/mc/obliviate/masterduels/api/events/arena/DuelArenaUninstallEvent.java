package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelArenaUninstallEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Game game;

	public DuelArenaUninstallEvent(Game game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}