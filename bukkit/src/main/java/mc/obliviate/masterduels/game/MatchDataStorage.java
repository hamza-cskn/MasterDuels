package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.game.round.MatchRoundData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purpose of class,
 * <p>
 * Raw storing datas of a game.
 * Game builders and game creators objects has any dependents
 * GameDataStorage doesn't have.
 */

/**
 * this object uses locked field as lock
 * when locked field is not null, match data storage is locked.
 **/
public class MatchDataStorage {

	private boolean locked;

	private final MatchRoundData gameRoundData = new MatchRoundData();
	private final MatchTeamManager gameTeamManager = new MatchTeamManager();
	private final List<GameRule> gameRules = new ArrayList<>();
	private static Duration endDelay;
	private long finishTime;
	private Duration matchDuration = Duration.ofMinutes(1); //in millis

	public List<GameRule> getGameRules() {
		return Collections.unmodifiableList(gameRules);
	}

	public void addRule(GameRule rule) {
		Preconditions.checkState(!isLocked(), "this object is locked");
		if (gameRules.contains(rule)) return;
		gameRules.add(rule);
	}

	public void removeRule(GameRule rule) {
		Preconditions.checkState(!isLocked(), "this object is locked");
		gameRules.remove(rule);
	}

	public void clearRules() {
		Preconditions.checkState(!isLocked(), "this object is locked");
		gameRules.clear();
	}

	public MatchRoundData getGameRoundData() {
		return gameRoundData;
	}

	public Duration getMatchDuration() {
		return matchDuration;
	}

	public void setMatchDuration(Duration duration) {
		Preconditions.checkState(!isLocked(), "this object is locked");
		this.matchDuration = duration;
	}

	public MatchRoundData getRoundData() {
		return gameRoundData;
	}

	public MatchTeamManager getGameTeamManager() {
		return gameTeamManager;
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

	public boolean isLocked() {
		return locked;
	}

	protected void lock(Match match) {
		gameRoundData.lock();
		gameTeamManager.lock(match);
		this.locked = true;
	}
}
