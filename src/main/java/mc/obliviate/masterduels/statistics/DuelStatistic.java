package mc.obliviate.masterduels.statistics;

import java.util.UUID;

public class DuelStatistic {

	private final UUID player;
	private final int wins;
	private final int loses;

	public DuelStatistic(UUID player, int wins, int loses) {
		this.player = player;
		this.wins = wins;
		this.loses = loses;
	}

	public int getLoses() {
		return loses;
	}

	public int getWins() {
		return wins;
	}

	public UUID getPlayerUniqueId() {
		return player;
	}
}
