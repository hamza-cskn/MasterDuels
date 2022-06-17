package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.arena.IGameBuilder;
import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.*;

public class GameBuilder implements IGameBuilder {

	public static final Map<UUID, GameBuilder> GAME_BUILDER_MAP = new HashMap<>();
	private final MasterDuels plugin;
	private final TeamBuilderManager teamBuilderManager = new TeamBuilderManager(this);
	private final UUID id;
	private final List<Player> players = new ArrayList<>();
	private final GameDataStorage gameDataStorage = new GameDataStorage();
	private Game game = null;

	public GameBuilder(final MasterDuels plugin) {
		this.plugin = plugin;
		this.id = UUID.randomUUID();

		createRandomizedTeams();

		GAME_BUILDER_MAP.put(id, this);
	}

	@Override
	public void createTeam(Player... players) {
		createTeam(Arrays.asList(players));
	}

	@Override
	public void createTeam(List<Player> players) {
		teamBuilderManager.registerNewTeam(players);
	}

	@Override
	public Game build() {
		final Arena arena = Arena.findArena(getTeamSize(), getTeamAmount());

		if (arena == null) {
			//arena could not found
			return null;
		}

		if (game != null) {
			throw new IllegalStateException("Game Builder already built before.");
		}

		final Game game = new Game(plugin, this, arena);

		teamBuilderManager.registerTeamsIntoGame(game);

		this.game = game;
		destroy();
		return game;
	}

	@Override
	public Map<Integer, ITeamBuilder> getTeamBuilders() {
		return teamBuilderManager.getTeams();
	}

	@Override
	public ITeamBuilder getTeamBuilder(Player player) {
		for (final ITeamBuilder teamBuilder : getTeamBuilders().values()) {
			if (teamBuilder.getMembers().contains(player)) return teamBuilder;
		}
		return null;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public int getTeamSize() {
		return gameDataStorage.getTeamSize();
	}

	@Override
	public GameBuilder setTeamSize(int teamSize) {
		gameDataStorage.setTeamSize(teamSize);
		createRandomizedTeams();
		return this;
	}

	@Override
	public int getTeamAmount() {
		return gameDataStorage.getTeamAmount();
	}

	@Override
	public GameBuilder setTeamAmount(int teamAmount) {
		gameDataStorage.setTeamAmount(teamAmount);
		createRandomizedTeams();
		return this;
	}

	@Override
	public void createRandomizedTeams() {
		teamBuilderManager.getTeams().clear();
		for (int i = 1; i <= getTeamAmount(); i++) {
			final List<Player> playerList = players.subList(Math.min(players.size(), (i - 1) * getTeamSize()), Math.min(players.size(), i * getTeamSize()));
			createTeam(playerList);
		}

	}

	@Override
	public IKit getKit() {
		return gameDataStorage.getKit();
	}

	@Override
	public GameBuilder setKit(IKit kit) {
		gameDataStorage.setKit(kit);
		return this;
	}

	@Override
	public int getTotalRounds() {
		return gameDataStorage.getTotalRounds();
	}

	@Override
	public GameBuilder setTotalRounds(int totalRounds) {
		gameDataStorage.setTotalRounds(totalRounds);
		return this;
	}

	@Override
	public int getFinishTime() {
		return gameDataStorage.getFinishTime();
	}

	@Override
	public GameBuilder setFinishTime(int finishTime) {
		gameDataStorage.setFinishTime(finishTime);
		return this;
	}

	@Override
	public List<GameRule> getGameRules() {
		return gameDataStorage.getGameRules();
	}

	@Override
	public void addGameRule(GameRule rule) {
		if (getGameRules().contains(rule)) return;
		getGameRules().add(rule);
	}

	@Override
	public void removeGameRule(GameRule rule) {
		getGameRules().remove(rule);
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public boolean addPlayer(final Player player) {
		final GameBuilder playerGameBuilder = GAME_BUILDER_MAP.get(player.getUniqueId());
		if (playerGameBuilder != null) {
			playerGameBuilder.destroy();
		}


		if (!player.isOnline()) {
			return false;
		}

		final IUser invitedUser = DataHandler.getUser(player.getUniqueId());

		if (invitedUser instanceof Member) {
			return false;
		}

		for (final GameBuilder builder : GAME_BUILDER_MAP.values()) {
			//todo try to join a game builder when you have a game creator.
			if (builder.getPlayers().contains(player)) {
				return false;
			}
		}

		final ITeamBuilder team = getAvailableTeam();
		if (team == null) return false;
		players.add(player);
		team.add(player);

		return true;
	}

	@Override
	public ITeamBuilder getAvailableTeam() {
		for (ITeamBuilder teamBuilder : teamBuilderManager.getTeams().values()) {
			if (teamBuilder.getMembers().size() < teamBuilder.getSize()) return teamBuilder;
		}
		return null;
	}

	@Override
	public void removePlayer(Player player) {
		players.remove(player);
	}

	@Override
	public void destroy() {
		//unregister game builder
		GAME_BUILDER_MAP.remove(id);
	}

	@Override
	public UUID getId() {
		return id;
	}
}
