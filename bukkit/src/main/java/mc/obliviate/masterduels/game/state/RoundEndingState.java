package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Game;

public class RoundEndingState implements GameState {

	private final Game match;

	public RoundEndingState(Game match) {
		this.match = match;
		init();
	}

	private void init() {
		match.getGameSpectatorManager().getOmniSpectatorStorage().unspectateAll();
		match.resetPlayers();
	}

	@Override
	public void next() {
		new RoundStartingState(match);
	}

	@Override
	public void leave(IMember player) {

	}

	@Override
	public void leave(ISpectator player) {

	}

	@Override
	public Game getMatch() {
		return match;
	}


	@Override
	public GameStateType getGameStateType() {
		return GameStateType.ROUND_ENDING;
	}
}
