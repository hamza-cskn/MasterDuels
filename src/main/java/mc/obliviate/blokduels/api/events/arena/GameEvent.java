package mc.obliviate.blokduels.api.events.arena;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;

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
