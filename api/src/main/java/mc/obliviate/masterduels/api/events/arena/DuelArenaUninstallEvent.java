package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IGame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelArenaUninstallEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IGame game;

	public DuelArenaUninstallEvent(IGame game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IGame getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
