package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.round.MatchRoundData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchPreRoundStartEvent extends Event implements MatchEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Match game;

	public DuelMatchPreRoundStartEvent(Match match) {
		this.game = match;
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

	public int getNewRound() {
		return game.getGameDataStorage().getGameRoundData().getCurrentRound() + 1;
	}

	public MatchRoundData getRoundData() {
		return game.getGameDataStorage().getGameRoundData();
	}
}
