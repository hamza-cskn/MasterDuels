package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.state.MatchState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchUninstallEvent extends Event implements MatchEvent {

	private final Match match;
	private final MatchState state;
	private final boolean naturalUninstall;
	private static final HandlerList handlers = new HandlerList();

	public DuelMatchUninstallEvent(Match match, MatchState state, boolean naturalUninstall) {
		this.match = match;
		this.state = state;
		this.naturalUninstall = naturalUninstall;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isNaturalUninstall() {
		return naturalUninstall;
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
