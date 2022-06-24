package mc.obliviate.masterduels.api.events.arena;

import mc.obliviate.masterduels.api.arena.IArena;
import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.MatchStateType;

public interface MatchEvent extends ArenaEvent {

	IMatch getMatch();

	default MatchStateType getGameState() {
		return getMatch().getMatchState().getMatchStateType();
	}

	@Override
	default IArena getArena() {
		return getMatch().getArena();
	}
}
