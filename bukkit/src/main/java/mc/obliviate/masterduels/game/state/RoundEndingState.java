package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.Member;

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
		match.setGameState(new RoundStartingState(match));
	}

	@Override
	public void leave(Member member) {
		//todo unleavable state
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
