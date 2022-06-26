package mc.obliviate.masterduels.statistics;

import java.util.UUID;

public class DuelStatistic {

	private final UUID playerUniqueId;
	private int wins;
	private int losses;

	public DuelStatistic(UUID playerUniqueId, int wins, int losses) {
		this.playerUniqueId = playerUniqueId;
		this.wins = wins;
		this.losses = losses;
	}

	public int getLosses() {
		return losses;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getWins() {
		return wins;
	}

	public UUID getPlayerUniqueId() {
		return playerUniqueId;
	}
}
