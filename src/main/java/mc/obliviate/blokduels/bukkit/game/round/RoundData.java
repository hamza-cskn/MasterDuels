package mc.obliviate.blokduels.bukkit.game.round;

import mc.obliviate.blokduels.bukkit.team.Team;

import java.util.HashMap;
import java.util.Map;

public class RoundData {

	private final Map<Team, Integer> teamWins = new HashMap<>();
	private int round = 0;
	private int totalRounds;

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
	public boolean addRound() {
		//remove 1 to compare as unadded.
		return ++round-1 < totalRounds;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public int getWins(final Team team) {
		return teamWins.getOrDefault(team,0);
	}

	public Map<Team, Integer> getTeamWins() {
		return teamWins;
	}
}
