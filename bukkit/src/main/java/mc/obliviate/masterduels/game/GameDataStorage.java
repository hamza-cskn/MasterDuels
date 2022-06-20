package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.kit.Kit;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of class,
 *
 * Raw storing datas of a game.
 * Game builders and game creators objects has any dependents
 * GameDataStorage doesn't have.
 */
public class GameDataStorage {

	private final List<GameRule> gameRules = new ArrayList<>();
	private int teamAmount = 2;
	private int teamSize = 1;
	private int totalRounds = 1;
	private int finishTime = 60;
	private IKit kit = null;

	public List<GameRule> getGameRules() {
		return gameRules;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public void setTeamAmount(int teamAmount) {
		this.teamAmount = teamAmount;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public IKit getKit() {
		return kit;
	}

	public void setKit(IKit kit) {
		this.kit = kit;
	}
}
