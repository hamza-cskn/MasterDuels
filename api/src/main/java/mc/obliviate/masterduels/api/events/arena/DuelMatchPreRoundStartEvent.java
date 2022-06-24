package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.IMatchRoundData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreRoundStartEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IMatch game;

	public DuelMatchPreRoundStartEvent(IMatch match) {
		this.game = match;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IMatch getMatch() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public int getNewRound() {
		return game.getGameDataStorage().getGameRoundData().getCurrentRound() + 1;
	}

	public IMatchRoundData getRoundData() {
		return game.getGameDataStorage().getGameRoundData();
	}
}
