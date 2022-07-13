package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.state.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchStateChangeEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Match game;
	private final MatchState oldState;
	private final MatchState newState;

	public DuelMatchStateChangeEvent(Match game, MatchState oldState, MatchState newState) {
		this.game = game;
		this.oldState = oldState;
		this.newState = newState;
	}

	public MatchState getNewState() {
		return newState;
	}

	public MatchState getOldState() {
		return oldState;
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
