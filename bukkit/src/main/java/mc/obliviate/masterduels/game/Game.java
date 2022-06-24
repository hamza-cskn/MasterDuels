package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.round.GameRoundData;
import mc.obliviate.masterduels.game.spectator.GameSpectatorManager;
import mc.obliviate.masterduels.game.state.GameEndingState;
import mc.obliviate.masterduels.game.state.GameState;
import mc.obliviate.masterduels.game.state.GameUninstallingState;
import mc.obliviate.masterduels.game.state.IdleState;
import mc.obliviate.masterduels.game.task.GameTaskManager;
import mc.obliviate.masterduels.kit.Kit;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Game implements IGame {

	public static final PlayerReset PLAYER_RESET = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	public static final PlayerReset RESET_WHEN_PLAYER_LEFT = new PlayerReset().excludeExp().excludeLevel().excludeInventory();
	private final Arena arena;
	private final GameDataStorage gameDataStorage;
	private final GameTaskManager gameTaskManager = new GameTaskManager();
	private final GameSpectatorManager gameSpectatorManager = new GameSpectatorManager(this);
	//private final IBossBarManager bossBarManager = BossBarHandler.createBossBarManager();

	private GameState gameState = new IdleState(this);

	public Game(Arena arena, GameDataStorage gameDataStorage) {
		this.arena = arena;
		this.gameDataStorage = gameDataStorage;
	}

	public void start() {
		if (!(gameState instanceof IdleState)) throw new IllegalStateException("this match has already started.");

		gameState.next();
	}

	public IMember getMember(UUID playerUniqueId) {
		return gameDataStorage.getGameTeamManager().getMember(playerUniqueId);
	}

	/**
	 * adds player to game
	 *
	 * @param player the player
	 * @param kit    kit of the player
	 * @param teamNo team no of player
	 */
	public void addPlayer(Player player, IKit kit, int teamNo) {
		gameDataStorage.getGameTeamManager().registerMember(player, kit, teamNo);
	}

	/**
	 * removes player from game
	 * if game doesn't contains param player
	 * the method would ignores it.
	 *
	 * @param playerUniqueId
	 */
	public void removePlayer(UUID playerUniqueId) {
		final IMember member = getMember(playerUniqueId);
		if (member == null) return;
		gameDataStorage.getGameTeamManager().unregisterMember(member);
	}

	public GameTaskManager getGameTaskManager() {
		return gameTaskManager;
	}

	public GameSpectatorManager getGameSpectatorManager() {
		return gameSpectatorManager;
	}

	public GameDataStorage getGameDataStorage() {
		return gameDataStorage;
	}

	public Arena getArena() {
		return arena;
	}


	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public GameState getGameState() {
		return gameState;
	}

	public List<IMember> getAllMembers() {
		return gameDataStorage.getGameTeamManager().getAllMembers();
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
		if (gameState instanceof GameUninstallingState) {
			Logger.severe("Uninstall Game method called twice.");
			return;
		}

		setGameState(new GameUninstallingState(this));
	}

	/**
	 * finishes match naturally.
	 */
	public void finish() {
		if (gameState instanceof GameEndingState) {
			Logger.severe("Finish Game method called twice.");
			return;
		}

		setGameState(new GameEndingState(this));
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
		for (final IMember member : gameDataStorage.getGameTeamManager().getAllMembers()) {
			PLAYER_RESET.reset(member.getPlayer());
			Kit.load((Kit) member.getKit(), member.getPlayer());
		}
	}

	public void broadcastInGame(final String node, final PlaceholderUtil placeholderUtil) {
		for (final IMember member : getAllMembers()) {
			MessageUtils.sendMessage(member.getPlayer(), node, placeholderUtil);
		}
	}

	public void broadcastInGame(final String node) {
		broadcastInGame(node, new PlaceholderUtil());
	}


	public void broadcastGameEnd() {
		final String mode = YamlStorageHandler.getConfig().getString("game-end-broadcast-mode", "SERVER_WIDE");

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

		GameRoundData roundData = gameDataStorage.getGameRoundData();
		if (roundData.getTeamWins().isEmpty()) return;

		final ITeam winnerTeam = roundData.getWinnerTeam();
		final List<ITeam> loserTeams = gameDataStorage.getGameTeamManager().getTeams().stream().filter(team -> !team.equals(winnerTeam)).collect(Collectors.toList());


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

}
