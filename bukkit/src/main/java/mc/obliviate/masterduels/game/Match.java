package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.game.round.MatchRoundData;
import mc.obliviate.masterduels.game.spectator.MatchSpectatorManager;
import mc.obliviate.masterduels.game.state.IdleState;
import mc.obliviate.masterduels.game.state.MatchEndingState;
import mc.obliviate.masterduels.game.state.MatchState;
import mc.obliviate.masterduels.game.state.MatchUninstallingState;
import mc.obliviate.masterduels.game.task.MatchTaskManager;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Match {

	public static final PlayerReset PLAYER_RESET = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	public static final PlayerReset RESET_WHEN_PLAYER_LEFT = new PlayerReset().excludeExp().excludeLevel().excludeInventory();

	private final Arena arena;
	private final MatchDataStorage matchDataStorage;
	private final MatchTaskManager gameTaskManager = new MatchTaskManager();
	private final MatchSpectatorManager gameSpectatorManager = new MatchSpectatorManager(this);
	private final List<Player> players;
	//private final IBossBarManager bossBarManager = BossBarHandler.createBossBarManager();

	private MatchState gameState = new IdleState(this);

	public Match(Arena arena, MatchDataStorage matchDataStorage) {
		this.arena = arena;
		this.matchDataStorage = matchDataStorage;

		final List<Player> players = new ArrayList<>();
		for (final Member member : matchDataStorage.getGameTeamManager().getAllMembers()) {
			players.add(member.getPlayer());
		}
		this.players = Collections.unmodifiableList(players);
	}

	public static MatchBuilder create() {
		return new MatchBuilder();
	}

	public static MatchBuilder create(MatchDataStorage matchDataStorage) {
		return new MatchBuilder(matchDataStorage);
	}


	public void start() {
		if (!(gameState instanceof IdleState)) throw new IllegalStateException("this match has already started.");

		gameState.next();
	}

	public Member getMember(UUID playerUniqueId) {
		return matchDataStorage.getGameTeamManager().getMember(playerUniqueId);
	}

	/**
	 * adds player to game
	 *
	 * @param player the player
	 * @param kit    kit of the player
	 * @param teamNo team no of player
	 */
	public void addPlayer(Player player, Kit kit, int teamNo) {
		matchDataStorage.getGameTeamManager().registerPlayer(player, kit, teamNo);
	}

	/**
	 * removes player from game
	 * if game doesn't contains param player
	 * the method would ignores it.
	 **/
	public void removeMember(Member member) {
		matchDataStorage.getGameTeamManager().unregisterMember(member);
	}

	public MatchTaskManager getGameTaskManager() {
		return gameTaskManager;
	}

	public MatchSpectatorManager getGameSpectatorManager() {
		return gameSpectatorManager;
	}

	public MatchDataStorage getGameDataStorage() {
		return matchDataStorage;
	}

	public Arena getArena() {
		return arena;
	}

	public void setGameState(MatchState gameState) {
		Bukkit.getPluginManager().callEvent(new DuelMatchStateChangeEvent(this, this.gameState, gameState));
		this.gameState = gameState;
	}

	public MatchState getMatchState() {
		return gameState;
	}

	public List<Member> getAllMembers() {
		return matchDataStorage.getGameTeamManager().getAllMembers();
	}

	public List<Player> getAllMembersAndSpectatorsAsPlayer() {
		final List<Player> allPlayers = new ArrayList<>();

		getAllMembers().forEach(member -> allPlayers.add(member.getPlayer()));
		gameSpectatorManager.getPureSpectatorStorage().getSpectatorList().forEach(spectator -> allPlayers.add(spectator.getPlayer()));

		return allPlayers;
	}

	/**
	 * uninstalls match instantly.
	 * unregisters match, members, spectators; clears arena etc...
	 */
	public void uninstall() {
		Logger.debug(Logger.DebugPart.GAME, "uninstall game - process started");
		if (gameState instanceof MatchUninstallingState) {
			Logger.severe("Uninstall Game method called twice.");
			return;
		}

		setGameState(new MatchUninstallingState(this));
	}

	/**
	 * finishes match naturally.
	 */
	public void finish() {
		if (gameState instanceof MatchEndingState) {
			Logger.severe("Finish Game method called twice.");
			return;
		}

		setGameState(new MatchEndingState(this));
	}

	/**
	 * Shows every player to param player
	 *
	 * @param player
	 */
	//todo vanish plugin compatibility
	public void showAll(Player player) {
		for (final Player p : getAllMembersAndSpectatorsAsPlayer()) {
			player.showPlayer(p);
			p.showPlayer(player);
		}
	}

	public void dropItems(final Player player, Location loc) {
		Preconditions.checkArgument(loc != null, "location cannot be null");
		Logger.debug(Logger.DebugPart.GAME, "drop items - process started");
		if (!getGameDataStorage().getGameRules().contains(GameRule.NO_DEAD_DROP)) return;
		if (MasterDuels.isInShutdownMode()) return;

		final List<ItemStack> allItemsInInventory = Arrays.asList(player.getInventory().getContents());
		allItemsInInventory.addAll(Arrays.asList(player.getInventory().getArmorContents()));

		allItemsInInventory.stream()
				.filter(item -> item != null && !item.getType().equals(Material.AIR))
				.forEach(item -> player.getWorld().dropItemNaturally(loc, item));

		player.getInventory().clear();
		Logger.debug(Logger.DebugPart.GAME, "drop items - process finished");
	}

	public void resetPlayers() {
		for (final Member member : matchDataStorage.getGameTeamManager().getAllMembers()) {
			PLAYER_RESET.reset(member.getPlayer());
			Kit.load((Kit) member.getKit(), member.getPlayer());
		}
	}

	public void broadcastInGame(final String node, final PlaceholderUtil placeholderUtil) {
		for (final Member member : getAllMembers()) {
			MessageUtils.sendMessage(member.getPlayer(), node, placeholderUtil);
		}
	}

	public void broadcastInGame(final String node) {
		broadcastInGame(node, new PlaceholderUtil());
	}


	public void broadcastGameEnd() {
		final String mode = ConfigurationHandler.getConfig().getString("game-end-broadcast-mode", "SERVER_WIDE");

		List<Player> receivers = null;

		switch (mode) {
			case "SERVER_WIDE":
				receivers = new ArrayList<>(Bukkit.getOnlinePlayers());
				break;
			case "SPECTATORS_AND_MEMBERS":
				receivers = getAllMembersAndSpectatorsAsPlayer();
				break;
			case "DISABLED":
				return;
		}

		if (receivers == null) return;

		MatchRoundData roundData = matchDataStorage.getGameRoundData();
		if (roundData.getTeamWins().isEmpty()) return;

		final Team winnerTeam = roundData.getWinnerTeam();
		final List<Team> loserTeams = matchDataStorage.getGameTeamManager().getTeams().stream().filter(team -> !team.equals(winnerTeam)).collect(Collectors.toList());


		if (roundData.getWinnerTeam().getSize() == 1) {
			final Player winner = roundData.getWinnerTeam().getMembers().get(0).getPlayer();
			final String loserName = loserTeams.size() == 0 || loserTeams.get(0).getSize() == 0 ? "" : Utils.getDisplayName(loserTeams.get(0).getMembers().get(0).getPlayer());
			for (final Player player : receivers) {
				MessageUtils.sendMessage(player, "game-end-broadcast.solo", new PlaceholderUtil().add("{winner}", Utils.getDisplayName(winner)).add("{loser}", loserName).add("{winner-health}", "" + winner.getHealthScale()));
			}
		} else {
			final Player winner = winnerTeam.getMembers().get(0).getPlayer();
			final String loserName = loserTeams.size() == 0 || loserTeams.get(0).getSize() == 0 ? "" : Utils.getDisplayName(loserTeams.get(0).getMembers().get(0).getPlayer());
			for (final Player player : receivers) {
				MessageUtils.sendMessage(player, "game-end-broadcast.non-solo", new PlaceholderUtil().add("{winner}", Utils.getDisplayName(winner)).add("{loser}", loserName));
			}
		}
	}


	public List<Player> getPlayers() {
		return players;
	}
}
