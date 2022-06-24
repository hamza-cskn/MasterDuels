package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.game.Match;

public class RoundEndingState implements MatchState {

	private final Match match;

	public RoundEndingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		match.getGameSpectatorManager().getSemiSpectatorStorage().unspectateAll();
		match.resetPlayers();
	}

	@Override
	public void next() {
		new RoundStartingState(match);
	}

	@Override
	public Match getMatch() {
		return match;
	}


	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.ROUND_ENDING;
	}
}
