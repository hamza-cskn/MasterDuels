package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.kit.IKit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IGameBuilder {

	void createTeam(Player... players);

	void createTeam(List<Player> players);

	IGame build();

	Map<Integer, ITeamBuilder> getTeamBuilders();

	ITeamBuilder getTeamBuilder(Player player);

	IGame getGame();

	int getTeamSize();

	IGameBuilder setTeamSize(int teamSize);

	int getTeamAmount();

	IGameBuilder setTeamAmount(int teamAmount);

	void createRandomizedTeams();

	IKit getKit();

	IGameBuilder setKit(IKit kit);

	int getTotalRounds();

	IGameBuilder setTotalRounds(int totalRounds);

	Duration getMatchDuration();

	IGameBuilder setMatchDuration(Duration duration);

	List<GameRule> getGameRules();

	void addGameRule(GameRule rule);

	void removeGameRule(GameRule rule);

	List<Player> getPlayers();

	boolean addPlayer(final Player player);

	ITeamBuilder getAvailableTeam();

	void removePlayer(Player player);

	void destroy();

	UUID getId();
}
