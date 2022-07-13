package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.state.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchRoundEndEvent extends Event implements MatchEvent {

	private final Match match;
	private final MatchState state;
	private static final HandlerList handlers = new HandlerList();

	public DuelMatchRoundEndEvent(Match match, MatchState state) {
		this.match = match;
		this.state = state;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public MatchState getState() {
		return state;
	}

	@Override
	public Match getMatch() {
		return match;
	}

}
