package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.arena.IArena;
import mc.obliviate.masterduels.api.arena.IGame;

public interface GameEvent extends ArenaEvent {

	IGame getGame();

	default GameStateType getGameState() {
		return getGame().getGameState().getGameStateType();
	}

	@Override
	default IArena getArena() {
		return getGame().getArena();
	}
}
