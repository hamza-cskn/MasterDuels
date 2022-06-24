package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.arena.IGameDataStorage;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.game.round.GameRoundData;
import mc.obliviate.masterduels.game.team.GameTeamManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of class,
 * <p>
 * Raw storing datas of a game.
 * Game builders and game creators objects has any dependents
 * GameDataStorage doesn't have.
 */
public class GameDataStorage implements IGameDataStorage {

	private final GameRoundData gameRoundData = new GameRoundData();
	private final GameTeamManager gameTeamManager = new GameTeamManager();
	private final List<GameRule> gameRules = new ArrayList<>();
	private static Duration endDelay;
	private long finishTime;
	private Duration matchDuration = Duration.ofMinutes(1); //in millis
	private IKit kit = null;

	public List<GameRule> getGameRules() {
		return gameRules;
	}

	public GameRoundData getGameRoundData() {
		return gameRoundData;
	}

	public Duration getMatchDuration() {
		return matchDuration;
	}

	public void setMatchDuration(Duration duration) {
		this.matchDuration = duration;
	}

	public GameRoundData getRoundData() {
		return gameRoundData;
	}

	public GameTeamManager getGameTeamManager() {
		return gameTeamManager;
	}

	public IKit getKit() {
		return kit;
	}

	public void setKit(IKit kit) {
		this.kit = kit;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public static Duration getEndDelay() {
		return endDelay;
	}

	public static void setEndDelay(Duration endDelay) {
		GameDataStorage.endDelay = endDelay;
	}
}
