package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.team.TeamBuilderManager;
import mc.obliviate.masterduels.user.DuelUser;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;

public class MatchBuilder {

	public static final Map<UUID, MatchBuilder> GAME_BUILDER_MAP = new HashMap<>();
	private final TeamBuilderManager teamBuilderManager = new TeamBuilderManager(this);
	private final UUID id;
	private final List<Player> players = new ArrayList<>();
	private final MatchDataStorage matchDataStorage = new MatchDataStorage();
	private Match match = null;

	public MatchBuilder() {
		this.id = UUID.randomUUID();

		createRandomizedTeams();

		GAME_BUILDER_MAP.put(id, this);
	}

	public void createTeam(Player... players) {
		createTeam(Arrays.asList(players));
	}

	public void createTeam(List<Player> players) {
		teamBuilderManager.registerNewTeam(players);
	}

	public Match build() {
		final Arena arena = Arena.findArena(getTeamSize(), getTeamAmount());

		if (arena == null) {
			//arena could not found
			return null;
		}

		if (match != null) {
			throw new IllegalStateException("Game Builder already built before.");
		}

		final Match game = new Match(arena, new MatchDataStorage());
		teamBuilderManager.registerTeamsIntoGame(game);

		this.match = game;
		destroy();
		return game;
	}

	public Map<Integer, ITeamBuilder> getTeamBuilders() {
		return teamBuilderManager.getTeams();
	}

	public ITeamBuilder getTeamBuilder(Player player) {
		for (final ITeamBuilder teamBuilder : getTeamBuilders().values()) {
			if (teamBuilder.getMembers().contains(player)) return teamBuilder;
		}
		return null;
	}

	public Match getMatch() {
		return match;
	}

	public int getTeamSize() {
		return matchDataStorage.getGameTeamManager().getTeamSize();
	}

	public MatchBuilder setTeamSize(int teamSize) {
		matchDataStorage.getGameTeamManager().setTeamSize(teamSize);
		createRandomizedTeams();
		return this;
	}

	public int getTeamAmount() {
		return matchDataStorage.getGameTeamManager().getTeamAmount();
	}

	public MatchBuilder setTeamAmount(int teamAmount) {
		matchDataStorage.getGameTeamManager().setTeamAmount(teamAmount);
		createRandomizedTeams();
		return this;
	}

	public void createRandomizedTeams() {
		teamBuilderManager.getTeams().clear();
		for (int i = 1; i <= getTeamAmount(); i++) {
			final List<Player> playerList = players.subList(Math.min(players.size(), (i - 1) * getTeamSize()), Math.min(players.size(), i * getTeamSize()));
			createTeam(playerList);
		}

	}

	public IKit getKit() {
		return matchDataStorage.getKit();
	}

	public MatchBuilder setKit(IKit kit) {
		matchDataStorage.setKit(kit);
		return this;
	}

	public int getTotalRounds() {
		return matchDataStorage.getGameRoundData().getTotalRounds();
	}

	public MatchBuilder setTotalRounds(int totalRounds) {
		matchDataStorage.getGameRoundData().setTotalRounds(totalRounds);
		return this;
	}

	public Duration getMatchDuration() {
		return matchDataStorage.getMatchDuration();
	}

	public MatchBuilder setMatchDuration(Duration matchTime) {
		matchDataStorage.setMatchDuration(matchTime);
		return this;
	}

	public List<GameRule> getGameRules() {
		return matchDataStorage.getGameRules();
	}

	public void addGameRule(GameRule rule) {
		if (getGameRules().contains(rule)) return;
		getGameRules().add(rule);
	}


	public void removeGameRule(GameRule rule) {
		getGameRules().remove(rule);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean addPlayer(final Player player) {
		final MatchBuilder playerMatchBuilder = GAME_BUILDER_MAP.get(player.getUniqueId());
		if (playerMatchBuilder != null) {
			playerMatchBuilder.destroy();
		}

		if (!player.isOnline()) {
			return false;
		}

		DuelUser duelUser = DuelUser.getDuelUser(player.getUniqueId());
		if (duelUser.isInMatchBuilder()) {
			return false;
		}

		final IUser user = DataHandler.getUser(player.getUniqueId());

		if (user instanceof Member) {
			return false;
		}

		for (final MatchBuilder builder : GAME_BUILDER_MAP.values()) {
			//todo try to join a game builder when you have a game creator.
			if (builder.getPlayers().contains(player)) {
				return false;
			}
		}

		final ITeamBuilder team = getAvailableTeam();
		if (team == null) return false;
		duelUser.setMatchBuilder(this);
		players.add(player);
		team.add(player);

		return true;
	}

	public ITeamBuilder getAvailableTeam() {
		for (ITeamBuilder teamBuilder : teamBuilderManager.getTeams().values()) {
			if (teamBuilder.getMembers().size() < teamBuilder.getSize()) return teamBuilder;
		}
		return null;
	}

	public void removePlayer(Player player) {
		DuelUser duelUser = DuelUser.getDuelUser(player.getUniqueId());
		duelUser.exitMatchBuilder();
		players.remove(player);
		teamBuilderManager.getTeam(player.getUniqueId()).remove(player);
	}

	public void destroy() {
		//unregister game builder
		for (Player player : players) {
			removePlayer(player);
		}
		GAME_BUILDER_MAP.remove(id);
	}

	public UUID getId() {
		return id;
	}
}
