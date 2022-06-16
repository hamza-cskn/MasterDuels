package mc.obliviate.masterduels.statistics;

import java.util.UUID;

public class DuelStatistic {

	private final UUID player;
	private final int wins;
	private final int losses;

	public DuelStatistic(UUID player, int wins, int losses) {
		this.player = player;
		this.wins = wins;
		this.losses = losses;
	}

	public int getLosses() {
		return losses;
	}

	public int getWins() {
		return wins;
	}

	public UUID getPlayerUniqueId() {
		return player;
	}
}
