package mc.obliviate.blokduels.api.events.arena;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.round.RoundData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGamePreRoundStartEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Game game;

	public DuelGamePreRoundStartEvent(Game game) {
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

	public int getNewRound() {
		return game.getRoundData().getCurrentRound() + 1;
	}

	public RoundData getRoundData() {
		return game.getRoundData();
	}
}
