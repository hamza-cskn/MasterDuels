package mc.obliviate.blokduels.game;

import com.sun.istack.internal.Nullable;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.gamerule.GameRule;
import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.invite.InviteResponse;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.user.User;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GameBuilder {

	private static final Map<UUID, GameBuilder> GAME_BUILDER_MAP = new HashMap<>();
	private final BlokDuels plugin;
	private final Map<UUID, Invite> invites = new HashMap<>();
	private final List<TeamBuilder> teamBuilders = new ArrayList<>();
	private final UUID owner;
	private final List<Player> players = new ArrayList<>();
	private final List<GameRule> gameRules = new ArrayList<>();
	private int teamAmount = 2;
	private int teamSize = 1;
	private int totalRounds = 1;
	private int finishTime = 60;
	private Kit kit = null;
	private Game game = null;
	private int createdTeamsAmount = 0;

	public GameBuilder(final BlokDuels plugin, UUID owner) {
		final GameBuilder builder = GAME_BUILDER_MAP.get(owner);
		if (builder != null) {
			builder.destory();
		}
		GAME_BUILDER_MAP.put(owner, this);
		this.plugin = plugin;
		this.owner = owner;
		//todo target is offline stack trace error is possible
		players.add(Bukkit.getPlayer(owner));
	}

	public static Map<UUID, GameBuilder> getGameBuilderMap() {
		return GAME_BUILDER_MAP;
	}

	public void createTeam(Player... players) {
		createTeam(Arrays.asList(players));
	}

	public void createTeam(List<Player> players) {
		if (players.size() != teamSize)
			throw new IllegalArgumentException("Team size is " + teamSize + " but given " + players.size() + " players.");

		if (createdTeamsAmount > teamSize)
			throw new IllegalStateException("Team amount is " + teamAmount + ". All teams has created already.");

		teamBuilders.add(new TeamBuilder(++createdTeamsAmount, teamSize, players));
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

		final Game game = new Game(plugin, this, totalRounds, arena, kit, finishTime, gameRules);

		for (final TeamBuilder builder : teamBuilders) {
			game.registerTeam(builder.build(game));
		}

		this.game = game;
		return game;
	}

	@Nullable
	public Game getGame() {
		return game;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public GameBuilder teamSize(int teamSize) {
		this.teamSize = teamSize;
		return this;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public GameBuilder teamAmount(int teamAmount) {
		this.teamAmount = teamAmount;
		return this;
	}

	public int getCreatedTeamsAmount() {
		return createdTeamsAmount;
	}

	public Map<UUID, Invite> getInvites() {
		return invites;
	}

	public void sendInvite(final Player inviter, final Player invited, final InviteResponse response) {
		if (invited == null) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			return;
		}

		final User invitedUser = DataHandler.getUser(invited.getUniqueId());

		if (invitedUser instanceof Member) {
			MessageUtils.sendMessage(inviter, "target-already-in-duel", new PlaceholderUtil().add("{target}", inviter.getName()));
			return;
		}

		for (final GameBuilder builder : GAME_BUILDER_MAP.values()) {
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

	public GameBuilder totalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
		return this;
	}

	public int getFinishTime() {
		return finishTime;
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

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public void destory() {
		for (final Invite invite : invites.values()) {
			invite.onExpire();
		}
	}

	public UUID getOwner() {
		return owner;
	}
}
