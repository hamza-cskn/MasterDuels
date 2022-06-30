package mc.obliviate.masterduels.game.round;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * this object uses locked field as lock
 * when locked field is not null, match round data is locked.
 **/
public class MatchRoundData {

	private boolean locked = false;

	private final Map<Team, Integer> teamWins = new HashMap<>();
	private Team winnerTeam;
	private int round = 0;
	private int totalRounds = 1;

	public void addWin(final Team team) {
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
		Preconditions.checkState(!isLocked(), "this object is locked");
		this.totalRounds = totalRounds;
	}

	public int getWins(final Team team) {
		return teamWins.getOrDefault(team, 0);
	}

	public Map<Team, Integer> getTeamWins() {
		return teamWins;
	}

	public Team getWinnerTeam() {
		return winnerTeam;
	}

	public void setWinnerTeam(Team winnerTeam) {
		this.winnerTeam = winnerTeam;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		this.locked = true;
	}
}
