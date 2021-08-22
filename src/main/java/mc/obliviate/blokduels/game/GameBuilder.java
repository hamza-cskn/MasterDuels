package mc.obliviate.blokduels.game;

import com.sun.istack.internal.Nullable;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.invite.InviteResponse;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.team.Team;
import org.bukkit.entity.Player;

import java.util.*;

public class GameBuilder {

	private final BlokDuels plugin;
	private final Arena arena;
	private final Map<UUID, Invite> invites = new HashMap<>();
	private int teamAmount = 1;
	private int teamSize = 1;
	private int totalRounds = 1;
	private int finishTime = 60;
	private Kit kit = new Kit(null);
	private Game game = null;
	private List<GameRules> gameRules = null;
	private int createdTeamsAmount = 0;

	public GameBuilder(final BlokDuels plugin, final Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	public void createTeam(Player... players) {
		if (players.length != teamSize)
			throw new IllegalArgumentException("Team size is " + teamSize + " but given " + players.length + " players.");

		if (createdTeamsAmount < teamSize)
			throw new IllegalStateException("Team amount is " + teamAmount + ". All teams has created already.");

		new Team(++createdTeamsAmount, teamSize, Arrays.asList(players), game);
	}

	public void removeInvite(final UUID uuid) {
		invites.remove(uuid);
	}

	public Game build() {
		if (game != null) {
			throw new IllegalStateException("Game Builder already built before.");
		}
		final Game game = new Game(plugin, this, totalRounds, arena, kit, finishTime, null);
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

	public void sendInvite(final Player inviter, final Player invited,final InviteResponse response) {
		final Invite invite = new Invite(plugin,inviter,invited,this);
		invite.onResponse(response);
	}

	public Arena getArena() {
		return arena;
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

	public List<GameRules> getGameRules() {
		return gameRules;
	}

	public GameBuilder setGameRules(List<GameRules> gameRules) {
		this.gameRules = gameRules;
		return this;
	}
}
