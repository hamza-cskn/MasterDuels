package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.GameState;
import mc.obliviate.masterduels.api.arena.IArena;
import mc.obliviate.masterduels.api.arena.IGame;

public interface GameEvent extends ArenaEvent {

	IGame getGame();

	default GameState getGameState() {
		return getGame().getGameState();
	}

	@Override
	default IArena getArena() {
		return getGame().getArena();
	}
}
