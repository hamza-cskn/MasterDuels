package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.arena.IGameRoundData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGamePreRoundStartEvent extends Event implements GameEvent {

	private static final HandlerList handlers = new HandlerList();

	private final IGame game;

	public DuelGamePreRoundStartEvent(IGame game) {
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

	public int getNewRound() {
		return game.getGameDataStorage().getGameRoundData().getCurrentRound() + 1;
	}

	public IGameRoundData getRoundData() {
		return game.getGameDataStorage().getGameRoundData();
	}
}
