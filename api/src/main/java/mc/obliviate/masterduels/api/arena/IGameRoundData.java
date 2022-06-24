package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.user.ITeam;

public interface IGameRoundData {

	void addWin(final ITeam team);

	int getCurrentRound();

	/**
	 * @return return is there more round?
	 * returns false when last round is finished.
	 */
	boolean nextRound();

	void setTotalRounds(int totalRounds);

}
