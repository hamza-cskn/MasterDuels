package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreStartEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Match game;

	public DuelMatchPreStartEvent(Match game) {
		this.game = game;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Match getMatch() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
