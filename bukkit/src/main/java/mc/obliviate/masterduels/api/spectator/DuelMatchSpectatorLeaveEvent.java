package mc.obliviate.masterduels.api.spectator;

import mc.obliviate.masterduels.api.arena.MatchEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.Spectator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchSpectatorLeaveEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Spectator spectator;

	public DuelMatchSpectatorLeaveEvent(Spectator spectator) {
		this.spectator = spectator;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Match getMatch() {
		return spectator.getMatch();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Spectator getSpectator() {
		return spectator;
	}
}
