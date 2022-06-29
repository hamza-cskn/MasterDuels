package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;

public interface MatchEvent extends ArenaEvent {

	Match getMatch();

	default MatchStateType getGameState() {
		return getMatch().getMatchState().getMatchStateType();
	}

	@Override
	default Arena getArena() {
		return getMatch().getArena();
	}
}
