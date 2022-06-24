package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.kit.IKit;

import java.time.Duration;
import java.util.List;

public interface IGameDataStorage {

	List<GameRule> getGameRules();

	IGameRoundData getGameRoundData();

	Duration getMatchDuration();

	void setMatchDuration(Duration duration);

	IGameTeamManager getGameTeamManager();

	IKit getKit();

	void setKit(IKit kit);

	long getFinishTime();

	void setFinishTime(long finishTime);

}
