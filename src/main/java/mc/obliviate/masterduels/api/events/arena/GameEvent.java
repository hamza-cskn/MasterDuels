package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameState;

public interface GameEvent extends ArenaEvent {

	Game getGame();

	default GameState getGameState() {
		return getGame().getGameState();
	}

	@Override
	default Arena getArena() {
		return getGame().getArena();
	}
}
