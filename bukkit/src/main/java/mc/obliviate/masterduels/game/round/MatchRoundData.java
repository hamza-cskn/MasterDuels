package mc.obliviate.masterduels.game.round;

import mc.obliviate.masterduels.api.arena.IMatchRoundData;
import mc.obliviate.masterduels.api.user.ITeam;

import java.util.HashMap;
import java.util.Map;

public class MatchRoundData implements IMatchRoundData {

	private final Map<ITeam, Integer> teamWins = new HashMap<>();
	private ITeam winnerTeam;
	private int round = 0;
	private int totalRounds = 1;

	@Override
	public void addWin(final ITeam team) {
		final int wins = teamWins.getOrDefault(team, 0);
		teamWins.put(team, wins + 1);
	}

	public int getCurrentRound() {
		return round;
	}

	/**
	 * @return return is there more round?
	 * returns false when last round is finished.
	 */
	public boolean nextRound() {
		if (didTeamWin()) return false;

		//compare as unadded
		return totalRounds > round++;
	}

	public boolean didTeamWin() {
		for (int wins : teamWins.values()) {
			//3 rounds, 2 win means no third round.
			if (wins > totalRounds / 2) {
				return true;
			}
		}
		return false;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public int getWins(final ITeam team) {
		return teamWins.getOrDefault(team, 0);
	}

	public Map<ITeam, Integer> getTeamWins() {
		return teamWins;
	}

	public ITeam getWinnerTeam() {
		return winnerTeam;
	}

	public void setWinnerTeam(ITeam winnerTeam) {
		this.winnerTeam = winnerTeam;
	}
}
