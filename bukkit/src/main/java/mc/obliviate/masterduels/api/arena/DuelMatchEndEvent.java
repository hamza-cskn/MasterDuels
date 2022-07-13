package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.state.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchEndEvent extends Event implements MatchEvent {

	private final Match match;
	private final MatchState state;
	private final boolean naturalEnding;
	private static final HandlerList handlers = new HandlerList();

	public DuelMatchEndEvent(Match match, MatchState state, boolean naturalEnding) {
		this.match = match;
		this.state = state;
		this.naturalEnding = naturalEnding;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean isNaturalEnding() {
		return naturalEnding;
	}

	public MatchState getState() {
		return state;
	}

	@Override
	public Match getMatch() {
		return match;
	}

}
