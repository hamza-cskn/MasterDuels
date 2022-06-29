package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.game.round.MatchRoundData;
import mc.obliviate.masterduels.game.team.MatchTeamManager;
import mc.obliviate.masterduels.kit.Kit;

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
public class MatchDataStorage {

	private final MatchRoundData gameRoundData = new MatchRoundData();
	private final MatchTeamManager gameTeamManager = new MatchTeamManager();
	private final List<GameRule> gameRules = new ArrayList<>();
	private static Duration endDelay;
	private long finishTime;
	private Duration matchDuration = Duration.ofMinutes(1); //in millis
	private Kit kit = null;

	public List<GameRule> getGameRules() {
		return gameRules;
	}

	public MatchRoundData getGameRoundData() {
		return gameRoundData;
	}

	public Duration getMatchDuration() {
		return matchDuration;
	}

	public void setMatchDuration(Duration duration) {
		this.matchDuration = duration;
	}

	public MatchRoundData getRoundData() {
		return gameRoundData;
	}

	public MatchTeamManager getGameTeamManager() {
		return gameTeamManager;
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
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
		MatchDataStorage.endDelay = endDelay;
	}
}
