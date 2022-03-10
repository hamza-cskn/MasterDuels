package mc.obliviate.masterduels.game;

import com.sun.istack.internal.Nullable;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.bet.Bet;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.invite.InviteResponse;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameBuilder {

	private static final Map<UUID, GameBuilder> GAME_BUILDER_MAP = new HashMap<>();
	private final MasterDuels plugin;
	private final Map<UUID, Invite> invites = new HashMap<>();
	private final TeamBuilderManager teamBuilderManager = new TeamBuilderManager(this);
	private final UUID id;
	private final List<Player> players = new ArrayList<>();
	private final List<GameRule> gameRules = new ArrayList<>();
	private final Bet bet = new Bet();
	private int teamAmount = 2;
	private int teamSize = 1;
	private int totalRounds = 1;
	private int finishTime = 60;
	private Kit kit = null;
	private Game game = null;

	public GameBuilder(final MasterDuels plugin) {

		this.plugin = plugin;
		this.id = UUID.randomUUID();

		createRandomizedTeams();

		GAME_BUILDER_MAP.put(id, this);

	}

	public static Map<UUID, GameBuilder> getGameBuilderMap() {
		return GAME_BUILDER_MAP;
	}

	public void createTeam(Player... players) {
		createTeam(Arrays.asList(players));
	}

	public void createTeam(List<Player> players) {
		teamBuilderManager.registerNewTeam(players);
	}

	public Game build() {
		final Arena arena = Arena.findArena(teamSize, teamAmount);

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
		return game;
	}

	public Map<Integer, TeamBuilder> getTeamBuilders() {
		return teamBuilderManager.getTeams();
	}

	public TeamBuilder getTeamBuilder(Player player) {
		for (final TeamBuilder teamBuilder : getTeamBuilders().values()) {
			if (teamBuilder.getMembers().contains(player)) return teamBuilder;
		}
		return null;
	}

	@Nullable
	public Game getGame() {
		return game;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public GameBuilder setTeamSize(int teamSize) {
		this.teamSize = teamSize;
		createRandomizedTeams();
		return this;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public GameBuilder setTeamAmount(int teamAmount) {
		this.teamAmount = teamAmount;
		createRandomizedTeams();
		return this;
	}

	public void createRandomizedTeams() {
		teamBuilderManager.getTeams().clear();
		for (int i = 1; i <= teamAmount; i++) {
			final List<Player> playerList = players.subList(Math.min(players.size(), (i - 1) * teamSize), Math.min(players.size(), i * teamSize));
			createTeam(playerList);
		}

	}

	public Map<UUID, Invite> getInvites() {
		return invites;
	}

	public void sendInvite(final Player inviter, final Player invited, final InviteResponse response) {
		if (invited == null) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			return;
		}

		final IUser invitedUser = DataHandler.getUser(invited.getUniqueId());

		if (invitedUser instanceof Member) {
			MessageUtils.sendMessage(inviter, "target-already-in-duel", new PlaceholderUtil().add("{target}", inviter.getName()));
			return;
		}

		for (final GameBuilder builder : GAME_BUILDER_MAP.values()) {
			if (builder.getId().equals(invited.getUniqueId())) break;
			if (builder.getPlayers().contains(invited)) {
				MessageUtils.sendMessage(inviter, "target-already-in-duel", new PlaceholderUtil().add("{target}", inviter.getName()));
				return;
			}
		}

		final Invite invite = new Invite(plugin, inviter, invited, this);
		invites.put(invited.getUniqueId(), invite);
		invite.onResponse(response);
	}

	public void removeInvite(final UUID uuid) {
		invites.remove(uuid);
	}

	public Kit getKit() {
		return kit;
	}

	public GameBuilder setKit(Kit kit) {
		this.kit = kit;
		return this;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public GameBuilder totalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
		return this;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public GameBuilder finishTime(int finishTime) {
		this.finishTime = finishTime;
		return this;
	}

	public List<GameRule> getGameRules() {
		return gameRules;
	}

	public void addGameRule(GameRule rule) {
		if (gameRules.contains(rule)) return;
		gameRules.add(rule);
	}

	public void removeGameRule(GameRule rule) {
		gameRules.remove(rule);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean addPlayer(final Player player) {
		final GameBuilder playerGameBuilder = GAME_BUILDER_MAP.get(player.getUniqueId());
		if (playerGameBuilder != null) {
			playerGameBuilder.destroy();
		}
		for (final Invite invite : findInvites(player.getUniqueId())) {
			invite.setResult(false);
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

		final TeamBuilder team = getAvailableTeam();
		if (team == null) return false;
		players.add(player);
		team.add(player);

		return true;
	}

	public TeamBuilder getAvailableTeam() {
		for (TeamBuilder teamBuilder : teamBuilderManager.getTeams().values()) {
			if (teamBuilder.getMembers().size() < teamBuilder.getSize()) return teamBuilder;
		}
		return null;
	}


	public List<Invite> findInvites(final UUID player) {
		final List<Invite> invites = new ArrayList<>();
		for (final GameBuilder builder : GAME_BUILDER_MAP.values()) {
			for (final UUID uuid : builder.getInvites().keySet()) {
				if (uuid.equals(player)) {
					invites.add(builder.getInvites().get(uuid));
				}
			}
		}
		return invites;
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public void destroy() {
		for (final Invite invite : invites.values()) {
			invite.onExpire();
		}
		GAME_BUILDER_MAP.remove(id);
	}

	public Bet getBet() {
		return bet;
	}

	public UUID getId() {
		return id;
	}
}
