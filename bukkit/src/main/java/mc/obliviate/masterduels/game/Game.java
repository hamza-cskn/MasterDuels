package mc.obliviate.masterduels.game;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.api.events.DuelGameMemberDeathEvent;
import mc.obliviate.masterduels.api.events.DuelGameTeamEleminateEvent;
import mc.obliviate.masterduels.api.arena.GameState;
import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.events.arena.*;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.bossbar.TABBossbarManager;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.game.round.RoundData;
import mc.obliviate.masterduels.game.spectator.GameSpectatorManager;
import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import mc.obliviate.masterduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.masterduels.utils.title.TitleHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mc.obliviate.masterduels.data.DataHandler.LOCK_TIME_IN_SECONDS;
import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class Game implements IGame {

	private static final PlayerReset RESET_WHEN_PLAYER_LEFT = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeAllowFlight().excludeGamemode();
	private static int endDelay = 20;
	private final MasterDuels plugin;
	private final Arena arena;
	private final Map<Integer, ITeam> teams = new HashMap<>();
	private final IKit kit;
	private final long finishTime;
	private final List<GameRule> gameRules;
	private final Map<String, BukkitTask> tasks = new HashMap<>();
	private final RoundData roundData = new RoundData();
	private final GameSpectatorManager spectatorManager = new GameSpectatorManager(this);
	private final GameBuilder gameBuilder;
	private final TABBossbarManager bossBarData = new TABBossbarManager(this);
	private final GameHistoryLog gameHistoryLog = new GameHistoryLog();
	private long timer;
	private GameState gameState = GameState.GAME_STARING;

	protected Game(final MasterDuels plugin, final GameBuilder gameBuilder, final Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
		this.gameBuilder = gameBuilder;
		this.kit = gameBuilder.getKit();
		this.finishTime = gameBuilder.getFinishTime();
		this.gameRules = gameBuilder.getGameRules();

		roundData.setTotalRounds(gameBuilder.getTotalRounds());
		DataHandler.registerGame(arena, this);
	}

	public static int getEndDelay() {
		return endDelay;
	}

	public static void setEndDelay(final int endDelay) {
		Game.endDelay = endDelay;
	}

	public static GameBuilder create(MasterDuels plugin) {
		return new GameBuilder(plugin);
	}

	protected void registerTeam(ITeam team) {
		teams.put(team.getTeamId(), team);
	}

	@Override
	public void startGame() {
		broadcastInGame("game-started");
		updateScoreboardTasks();
		storeKits();
		nextRound();

		for (final IMember member : getAllMembers()) {
			showAll(member.getPlayer());
		}

	}

	@Override
	public void initBossBar() {
		for (final IMember member : getAllMembers()) {
			bossBarData.show(member);
		}
		bossBarData.init();
	}

	@Override
	public void updateScoreboardTasks() {
		for (final IMember member : getAllMembers()) {
			updateScoreboardTask(member);
		}
	}

	private void updateScoreboardTask(final IMember member) {
		ScoreboardManager.uninstall(member.getPlayer());
		task("SCOREBOARDTASK_update_" + member.getPlayer().getName(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			ScoreboardManager.update(member, false);
		}, 0, 20));
	}

	@Override
	public void nextRound() {

		if (roundData.getCurrentRound() != 0) {
			Bukkit.getPluginManager().callEvent(new DuelGameRoundEndEvent(this));
		} else {
			Bukkit.getPluginManager().callEvent(new DuelGameStartEvent(this));
		}

		Bukkit.getPluginManager().callEvent(new DuelGamePreRoundStartEvent(this));

		if (!roundData.addRound()) {
			finishGame();
			return;
		}
		timer = System.currentTimeMillis() + (LOCK_TIME_IN_SECONDS * 1000L);
		setGameState(GameState.ROUND_STARTING);
		resetPlayers();
		reloadKits();
		lockTeams();
		spectatorManager.getOmniSpectatorStorage().unspectateAll();

		task("ROUNDTASK_on-round-start-timer", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			setGameState(GameState.BATTLE);
			onRoundStart(roundData.getCurrentRound());

		}, LOCK_TIME_IN_SECONDS * 20L + 1));
	}

	@Override
	public void onRoundStart(final int round) {
		broadcastInGame("round-has-started", new PlaceholderUtil().add("{round}", round + ""));
		updateScoreboardTasks();
		setFinishTimer();
		if (round == 1) {
			initBossBar();
		}

		for (final IMember member : getAllMembers()) {
			HCore.sendTitle(member.getPlayer(), TitleHandler.getTitle(TitleHandler.TitleType.ROUND_STARTED));
		}

		Bukkit.getPluginManager().callEvent(new DuelGameRoundStartEvent(this));
	}

	private void setFinishTimer() {
		timer = System.currentTimeMillis() + (finishTime * 1000);
		task("REMAINING_TIME", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			broadcastInGame("game-timed-out");
			uninstallGame();
		}, finishTime * 20));
	}

	@Override
	public void storeKits() {
		if (kit == null) return; //no kit, no store
		for (final IMember member : getAllMembers()) {
			if (!Kit.storeKits(member.getPlayer())) {
				Logger.error(member.getPlayer().getName() + "'s inventory could not stored! Duel game cancelling for security.");
				uninstallGame();
			}
		}
	}

	@Override
	public void reloadKits() {
		for (final IMember member : getAllMembers()) {
			Kit.load((Kit) kit, member.getPlayer());
		}
	}

	@Override
	public void finishRound() {
		cancelTasks("ROUNDTASK");
		broadcastInGame("round-has-finished", new PlaceholderUtil().add("{round}", roundData.getCurrentRound() + ""));
	}

	private void broadcastInGame(final String node) {
		broadcastInGame(node, new PlaceholderUtil());
	}

	@Override
	public List<IMember> getAllMembers() {
		final List<IMember> members = new ArrayList<>();
		for (final ITeam team : teams.values()) {
			members.addAll(team.getMembers());
		}
		return members;
	}

	@Override
	public List<Player> getAllMembersAndSpectatorsAsPlayer() {
		final List<Player> allPlayers = new ArrayList<>();
		getAllMembers().forEach(mem -> allPlayers.add(mem.getPlayer()));

		allPlayers.addAll(spectatorManager.getPureSpectatorStorage().getSpectatorList());

		return allPlayers;
	}

	private void broadcastInGame(final String node, final PlaceholderUtil placeholderUtil) {
		for (final IMember member : getAllMembers()) {
			MessageUtils.sendMessage(member.getPlayer(), node, placeholderUtil);
		}
	}

	/**
	 * natural finish game method
	 */
	@Override
	public void finishGame() {
		if (gameState.equals(GameState.GAME_ENDING)) {
			Logger.severe("Finish Game method called twice.");
			return;
		}

		Bukkit.getPluginManager().callEvent(new DuelGameFinishEvent(this));

		if (USE_PLAYER_INVENTORIES) {
			for (final ITeam team : getTeams().values()) {
				if (checkTeamEliminated(team)) {
					for (final IMember member : team.getMembers()) {
						dropItems(member.getPlayer());
					}
				}
			}
		}

		setGameState(GameState.GAME_ENDING);
		cancelTasks("REMAINING_TIME");
		timer = endDelay * 1000L + System.currentTimeMillis();
		Bukkit.getScheduler().runTaskLater(plugin, this::uninstallGame, endDelay * 20L);
	}

	private void broadcastGameEnd() {
		final String mode = plugin.getDatabaseHandler().getConfig().getString("game-end-broadcast-mode", "SERVER_WIDE");

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
		if (gameHistoryLog.getWinners().isEmpty()) return;

		if (gameHistoryLog.getWinners().size() == 1) {
			final Player winner = Bukkit.getPlayer(gameHistoryLog.getWinners().get(0));
			final String loserName = gameHistoryLog.getLosers().size() == 0 ? "" : Utils.getDisplayName(Bukkit.getOfflinePlayer(gameHistoryLog.getLosers().get(0)));
			for (final Player player : receivers) {
				MessageUtils.sendMessage(player, "game-end-broadcast.solo", new PlaceholderUtil().add("{winner}", Utils.getDisplayName(winner)).add("{loser}", loserName).add("{winner-health}", "" + winner.getHealthScale()));
			}
		} else {
			final Player winner = Bukkit.getPlayer(gameHistoryLog.getWinners().get(0));
			final String loserName = Utils.getDisplayName(Bukkit.getOfflinePlayer(gameHistoryLog.getLosers().get(0)));
			for (final Player player : receivers) {
				MessageUtils.sendMessage(player, "game-end-broadcast.non-solo", new PlaceholderUtil().add("{winner}", Utils.getDisplayName(winner)).add("{loser}", loserName));
			}
		}
	}

	/**
	 * force to finish game method
	 */
	@Override
	public void uninstallGame() {
		if (gameState.equals(GameState.UNINSTALLING)) {
			Logger.severe("Uninstall Game method called twice.");
			return;
		}

		Bukkit.getPluginManager().callEvent(new DuelArenaUninstallEvent(this));

		setGameState(GameState.UNINSTALLING);
		broadcastInGame("game-finished");

		for (final Player p : getAllMembersAndSpectatorsAsPlayer()) {
			leave(p);
		}

		cancelTasks(null);
		DataHandler.registerArena(arena);
	}

	@Override
	public void dropItems(final Player player) {
		if (!gameRules.contains(GameRule.NO_DEAD_DROP)) return;
		if (!MasterDuels.isInShutdownMode()) {
			final Location loc = player.getLocation();
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					player.getWorld().dropItemNaturally(loc, item);
				}
			}
			for (ItemStack item : player.getInventory().getArmorContents()) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					player.getWorld().dropItemNaturally(loc, item);
				}
			}
			player.getInventory().clear();
		}

	}

	@Override
	public void leave(final Player player) {
		leave(DataHandler.getUser(player.getUniqueId()));
	}

	@Override
	public void leave(final IUser user) {
		if (user instanceof Member) {
			leave((Member) user);
		} else if (user instanceof ISpectator) {
			leave((ISpectator) user);
		}
	}

	@Override
	public void leave(final ISpectator spectator) {
		spectatorManager.unspectate(spectator.getPlayer());
		RESET_WHEN_PLAYER_LEFT.reset(spectator.getPlayer());
		MessageUtils.sendMessage(spectator.getPlayer(), "you-left-from-duel");
		teleportToLobby(spectator.getPlayer());
	}

	@Override
	public void leave(final IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;
		if (!member.getTeam().getGame().equals(this))
			throw new IllegalStateException("Member " + member.getPlayer().getName() + " called leave method of another game object.");

		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().removeMember(member);

		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("Inventory could not restored: " + member.getPlayer());
		}

		showAll(member.getPlayer());

		RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
		ScoreboardManager.defaultScoreboard(member.getPlayer());


		teleportToLobby(member.getPlayer());

		MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		bossBarData.hide(member);

		if (member.getTeam().getMembers().size() == 0) {
			if (gameState.equals(GameState.GAME_ENDING) || gameState.equals(GameState.UNINSTALLING)) return;
			if (gameState.equals(GameState.BATTLE) && getAllMembers().size() > 0) {
				Logger.debug("Game finishing in 20s");
				finishGame();
			} else {
				Logger.debug("Game finishing...");
				uninstallGame();
			}
		}
	}

	@Override
	public boolean isMember(Player player) {
		for (IMember member : getAllMembers()) {
			if (member.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}

	private void teleportToLobby(final Player player) {
		if (DataHandler.getLobbyLocation() != null) {
			if (DataHandler.getLobbyLocation().getWorld() != null) {
				if (!player.teleport(DataHandler.getLobbyLocation())) {
					if (!MasterDuels.isInShutdownMode()) {
						player.kickPlayer("You could not teleported to lobby.\n" + DataHandler.getLobbyLocation());
						Logger.error("Player " + player.getName() + " could not teleported to lobby. MasterDuels kicked him.");
					}
				}
			}
		}
	}

	//todo search vanish plugin compatibility
	private void showAll(Player player) {
		for (final Player p : getAllMembersAndSpectatorsAsPlayer()) {
			player.showPlayer(p);

		}
	}

	@Override
	public void resetPlayers() {
		final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
		for (final IMember member : getAllMembers()) {
			playerReset.reset(member.getPlayer());
		}
	}

	@Override
	public void lockTeams() {
		for (final ITeam team : teams.values()) {
			lockTeam(team);
		}
	}

	@Override
	public void onDeath(final IMember victim, final IMember attacker) {
		final DuelGameMemberDeathEvent duelGameMemberDeathEvent = new DuelGameMemberDeathEvent(victim, attacker);
		Bukkit.getPluginManager().callEvent(duelGameMemberDeathEvent);

		if (attacker == null) {
			broadcastInGame("player-dead.without-attacker", new PlaceholderUtil().add("{victim}", Utils.getDisplayName(victim.getPlayer())));
		} else {
			broadcastInGame("player-dead.by-attacker", new PlaceholderUtil().add("{attacker}", Utils.getDisplayName(attacker.getPlayer())).add("{victim}", Utils.getDisplayName(victim.getPlayer())));
		}

		spectatorManager.spectate(victim);

		if (checkTeamEliminated(victim.getTeam())) {

			new DuelGameTeamEleminateEvent(victim.getTeam(), duelGameMemberDeathEvent);

			if (victim.getTeam().getMembers().size() > 1) {
				broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", Utils.getDisplayName(victim.getPlayer())));
			}
			nextRound();
		}
	}

	@Override
	public void spectate(Player player) {
		if (DataHandler.getUser(player.getUniqueId()) == null) {
			getSpectatorManager().spectate(player);
		}
	}

	//todo test: start a game when player is spectating
	@Override
	public void unspectate(Player player) {
		getSpectatorManager().unspectate(player);
	}

	@Override
	public boolean checkTeamEliminated(final ITeam team) {
		for (IMember p : team.getMembers()) {
			if (!spectatorManager.getOmniSpectatorStorage().getSpectatorList().contains(p.getPlayer())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void lockTeam(final ITeam team) {
		for (int i = 1; i <= (LOCK_TIME_IN_SECONDS * 10); i++) {
			task("ROUNDTASK_team-lock-" + team.getTeamId() + "_" + i, Bukkit.getScheduler().runTaskLater(plugin, () -> {
				teleportToLockPosition(team);
			}, i * 2L));

			if (i == 1 || i % 10 == 0) {
				task("ROUNDTASK_team-sendTitle-" + team.getTeamId() + "_" + i, Bukkit.getScheduler().runTaskLater(plugin, () -> {
					for (IMember member : getAllMembers()) {
						HCore.sendTitle(member.getPlayer(), TitleHandler.getTitle(TitleHandler.TitleType.ROUND_STARTING,
								new PlaceholderUtil().add("{round}", roundData.getCurrentRound() + "")
										.add("{remaining-time-second}", ((int) (timer + 100 - System.currentTimeMillis()) / 1000) + "")
										.add("{remaining-time-timer}", TimerUtils.formatTimeUntilThenAsTimer(timer + 100))
										.add("{remaining-time-time}", TimerUtils.formatTimeUntilThenAsTime(timer + 100))));
					}
				}, i * 2L));
			}
		}
	}

	@Override
	public void teleportToLockPosition(final ITeam team) {
		int i = 1;
		final Positions positions = arena.getPositions().get("spawn-team-" + team.getTeamId());
		if (positions == null) {
			Logger.severe("Player could not teleported to lock position because location set is null.");
			return;
		}
		for (final IMember member : team.getMembers()) {
			final Location loc = positions.getLocation(i++);
			if (loc == null) {
				Logger.severe("Player could not teleported to lock position because location is null.");
			}
			if (!member.getPlayer().teleport(loc)) {
				Logger.error("Player " + member.getPlayer().getName() + " could not teleported to duel arena. Game has been cancelled.");
				uninstallGame();
			}

		}
	}

	@Override
	public void cancelTasks(String prefix) {
		for (final Map.Entry<String, BukkitTask> task : tasks.entrySet()) {
			if (prefix == null || task.getKey().startsWith(prefix)) {
				task.getValue().cancel();
			}
		}
	}

	@Override
	public GameBuilder getGameBuilder() {
		return gameBuilder;
	}

	@Override
	public GameState getGameState() {
		return gameState;
	}

	private void setGameState(GameState gameState) {
		final GameStateChangeEvent gameStateChangeEvent = new GameStateChangeEvent(this, this.gameState);
		this.gameState = gameState;
		Bukkit.getPluginManager().callEvent(gameStateChangeEvent);
		onGameStateChange();
	}

	private void onGameStateChange() {
		if (gameState.equals(GameState.BATTLE)) {
			gameHistoryLog.setStartTime(System.currentTimeMillis());
		} else if (gameState.equals(GameState.GAME_ENDING)) {
			gameHistoryLog.finish(this);
			broadcastGameEnd();
		}
	}

	@Override
	public RoundData getRoundData() {
		return roundData;
	}

	@Override
	public GameSpectatorManager getSpectatorManager() {
		return spectatorManager;
	}

	@Override
	public Arena getArena() {
		return arena;
	}

	@Override
	public IKit getKit() {
		return kit;
	}

	@Override
	public List<GameRule> getGameRules() {
		return gameRules;
	}

	@Override
	public Map<Integer, ITeam> getTeams() {
		return teams;
	}

	@Override
	public long getFinishTime() {
		return finishTime;
	}

	@Override
	public Map<String, BukkitTask> getTasks() {
		return tasks;
	}

	public void task(final String taskName, final BukkitTask task) {
		final BukkitTask t = tasks.get(taskName);
		if (t != null) t.cancel();
		tasks.put(taskName, task);
	}

	@Override
	public long getTimer() {
		return timer;
	}

	public MasterDuels getPlugin() {
		return plugin;
	}
}
