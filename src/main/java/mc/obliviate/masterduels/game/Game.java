package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.DuelGameMemberDeathEvent;
import mc.obliviate.masterduels.api.events.DuelGameTeamEleminateEvent;
import mc.obliviate.masterduels.api.events.arena.*;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.bossbar.TABBossbarManager;
import mc.obliviate.masterduels.game.bet.Bet;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.game.round.RoundData;
import mc.obliviate.masterduels.game.spectator.GameSpectatorManager;
import mc.obliviate.masterduels.game.spectator.PureSpectatorStorage;
import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.user.team.Team;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
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

import java.util.*;

import static mc.obliviate.masterduels.data.DataHandler.LOCK_TIME_IN_SECONDS;
import static mc.obliviate.masterduels.game.GameState.*;
import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class Game {

	private static int endDelay = 20;
	private final MasterDuels plugin;
	private final Arena arena;
	private final Map<Integer, Team> teams = new HashMap<>();
	private final Kit kit;
	private final long finishTime;
	private final List<GameRule> gameRules;
	private final Map<String, BukkitTask> tasks = new HashMap<>();
	private final RoundData roundData = new RoundData();
	private final GameSpectatorManager spectatorManager = new GameSpectatorManager(this);
	private final GameBuilder gameBuilder;
	private final TABBossbarManager bossBarData = new TABBossbarManager(this);
	private final GameHistoryLog gameHistoryLog = new GameHistoryLog();
	private final Bet bet;
	private long timer;
	private GameState gameState = GAME_STARING;

	protected Game(final MasterDuels plugin, final GameBuilder gameBuilder, final Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
		this.gameBuilder = gameBuilder;
		this.kit = gameBuilder.getKit();
		this.finishTime = gameBuilder.getFinishTime();
		this.gameRules = gameBuilder.getGameRules();
		this.bet = gameBuilder.getBet();

		roundData.setTotalRounds(gameBuilder.getTotalRounds());
		DataHandler.registerGame(arena, this);
	}

	public static int getEndDelay() {
		return endDelay;
	}

	public static void setEndDelay(final int endDelay) {
		Game.endDelay = endDelay;
	}

	public static GameBuilder create(MasterDuels plugin, UUID owner) {
		return new GameBuilder(plugin, owner);
	}

	protected void registerTeam(Team team) {
		teams.put(team.getTeamId(), team);
	}

	public void startGame() {
		broadcastInGame("game-started");
		updateScoreboardTasks();
		storeKits();
		nextRound();

		for (final Member member : getAllMembers()) {
			bet.remove(member.getPlayer());
		}

		GameBuilder.getGameBuilderMap().get(gameBuilder.getOwner()).destroy();
	}

	public void initBossBar() {
		for (final Member member : getAllMembers()) {
			bossBarData.show(member);
		}
		bossBarData.init();
	}

	public void updateScoreboardTasks() {
		for (final Member member : getAllMembers()) {
			updateScoreboardTask(member);
		}
	}

	private void updateScoreboardTask(final Member member) {
		ScoreboardManager.uninstall(member.getPlayer());
		task("SCOREBOARDTASK_update_" + member.getPlayer().getName(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			ScoreboardManager.update(member, false);
		}, 0, 20));
	}

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
		setGameState(ROUND_STARTING);
		resetPlayers();
		reloadKits();
		lockTeams();
		spectatorManager.getOmniSpectatorStorage().unSpectateMembers();

		task("ROUNDTASK_on-round-start-timer", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			gameState = BATTLE;
			onRoundStart(roundData.getCurrentRound());
		}, LOCK_TIME_IN_SECONDS * 20L + 1));
	}

	public void onRoundStart(final int round) {
		broadcastInGame("round-has-started", new PlaceholderUtil().add("{round}", round + ""));
		updateScoreboardTasks();
		setFinishTimer();
		if (round == 1) {
			initBossBar();
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

	public void storeKits() {
		if (kit == null) return; //no kit, no store
		for (final Member member : getAllMembers()) {
			if (!Kit.storeKits(member.getPlayer())) {
				Logger.error(member.getPlayer().getName() + "'s inventory could not stored! Duel game cancelling for security.");
				uninstallGame();
			}
		}
	}

	public void reloadKits() {
		for (final Member member : getAllMembers()) {
			Kit.load(kit, member.getPlayer());
		}
	}

	public void finishRound() {
		cancelTasks("ROUNDTASK");
		broadcastInGame("round-has-finished", new PlaceholderUtil().add("{round}", roundData.getCurrentRound() + ""));
	}

	private void broadcastInGame(final String node) {
		broadcastInGame(node, new PlaceholderUtil());
	}

	public List<Member> getAllMembers() {
		final List<Member> members = new ArrayList<>();
		for (final Team team : teams.values()) {
			members.addAll(team.getMembers());
		}
		return members;
	}

	public List<Player> getAllMembersAndSpectatorsAsPlayer() {
		final List<Player> allPlayers = new ArrayList<>();
		getAllMembers().forEach(mem -> allPlayers.add(mem.getPlayer()));

		allPlayers.addAll(spectatorManager.getPureSpectatorStorage().getSpectatorList());

		return allPlayers;
	}

	private void broadcastInGame(final String node, final PlaceholderUtil placeholderUtil) {
		for (final Member member : getAllMembers()) {
			MessageUtils.sendMessage(member.getPlayer(), node, placeholderUtil);
		}
	}

	/**
	 * natural finish game method
	 */
	public void finishGame() {
		if (gameState.equals(GAME_ENDING)) {
			Logger.severe("Finish Game method called twice.");
			return;
		}

		Bukkit.getPluginManager().callEvent(new DuelGameFinishEvent(this));

		if (USE_PLAYER_INVENTORIES) {
			for (final Team team : getTeams().values()) {
				if (checkTeamEliminated(team)) {
					for (final Member member : team.getMembers()) {
						dropItems(member.getPlayer());
					}
				}
			}
		}

		for (final Member member : getAllMembers()) {
			bet.delivery(member.getPlayer());
		}

		setGameState(GAME_ENDING);
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
			for (final Player player : receivers) {
				final Player winner = Bukkit.getPlayer(gameHistoryLog.getWinners().get(0));
				final String loserName = gameHistoryLog.getLosers().size() == 0 ? "" : Bukkit.getOfflinePlayer(gameHistoryLog.getLosers().get(0)).getName();
				MessageUtils.sendMessage(player, "game-end-broadcast.solo", new PlaceholderUtil().add("{winner}", winner.getName()).add("{loser}", loserName).add("{winner-health}", "" + winner.getHealthScale()));
			}
		} else {
			for (final Player player : receivers) {
				final Player winner = Bukkit.getPlayer(gameHistoryLog.getWinners().get(0));
				final String loserName = Bukkit.getOfflinePlayer(gameHistoryLog.getLosers().get(0)).getName();

				MessageUtils.sendMessage(player, "game-end-broadcast.non-solo", new PlaceholderUtil().add("{winner}", winner.getName()).add("{loser}", loserName));
			}
		}
	}

	/**
	 * force to finish game method
	 */
	public void uninstallGame() {
		if (gameState.equals(UNINSTALLING)) {
			Logger.severe("Uninstall Game method called twice.");
			return;
		}

		Bukkit.getPluginManager().callEvent(new DuelArenaUninstallEvent(this));

		setGameState(UNINSTALLING);
		broadcastInGame("game-finished");

		for (final Player p : getAllMembersAndSpectatorsAsPlayer()) {
			leave(p);
		}

		cancelTasks(null);
		DataHandler.registerArena(arena);
	}


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

	public void leave(final Player player) {
		leave(DataHandler.getUser(player.getUniqueId()));
	}

	public void leave(final IUser user) {
		if (user instanceof Member) {
			leave((Member) user);
		} else if (user instanceof Spectator) {
			leave((Spectator) user);
		}
	}

	public void leave(final Spectator spectator) {
		spectatorManager.unspectate(spectator.getPlayer());
	}

	public boolean isMember(Player player) {
		for (Member member : getAllMembers()) {
			if (member.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}

	public void leave(final Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().removeMember(member);

		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("Inventory could not restored: " + member.getPlayer());
		}

		showAll(member.getPlayer());

		new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeGamemode().reset(member.getPlayer());
		ScoreboardManager.defaultScoreboard(member.getPlayer());


		if (DataHandler.getLobbyLocation() != null && DataHandler.getLobbyLocation().getWorld() != null && !member.getPlayer().teleport(DataHandler.getLobbyLocation())) {
			if (!MasterDuels.isInShutdownMode()) {
				member.getPlayer().kickPlayer("You could not teleported to lobby.\n" + DataHandler.getLobbyLocation());
				Logger.error("Player " + member.getPlayer().getName() + " could not teleported to lobby. BlokDuels kicked him.");
			}
		}

		MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		bossBarData.hide(member);

		if (member.getTeam().getMembers().size() == 0) {
			if (gameState.equals(GAME_ENDING) || gameState.equals(UNINSTALLING)) return;
			if (gameState.equals(BATTLE) && getAllMembers().size() > 0) {
				Logger.debug("Game finishing in 20s");
				finishGame();
			} else {
				Logger.debug("Game finishing...");
				uninstallGame();
			}
		}
	}

	private void showAll(Player player) {
		for (final Team team : teams.values()) {
			for (final Member m : team.getMembers()) {
				player.showPlayer(m.getPlayer());
			}
		}

		for (final Player p : spectatorManager.getPureSpectatorStorage().getSpectatorList()) {
			player.showPlayer(p);
		}
	}

	public void resetPlayers() {
		final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
		for (final Member member : getAllMembers()) {
			playerReset.reset(member.getPlayer());
		}
	}

	public void lockTeams() {
		for (final Team team : teams.values()) {
			lockTeam(team);
		}
	}

	public void onDeath(final Member victim, final Member attacker) {
		final DuelGameMemberDeathEvent duelGameMemberDeathEvent = new DuelGameMemberDeathEvent(victim, attacker);
		Bukkit.getPluginManager().callEvent(duelGameMemberDeathEvent);

		if (attacker == null) {
			broadcastInGame("player-dead.without-attacker", new PlaceholderUtil().add("{victim}", victim.getPlayer().getName()));
		} else {
			broadcastInGame("player-dead.by-attacker", new PlaceholderUtil().add("{attacker}", attacker.getPlayer().getName()).add("{victim}", victim.getPlayer().getName()));
		}

		spectatorManager.spectate(victim);

		if (checkTeamEliminated(victim.getTeam())) {

			new DuelGameTeamEleminateEvent(victim.getTeam(), duelGameMemberDeathEvent);

			if (victim.getTeam().getMembers().size() > 1) {
				broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", victim.getPlayer().getName()));
			}
			nextRound();
		}
	}

	public void spectate(Player player) {
		if (DataHandler.getUser(player.getUniqueId()) == null) {
			getSpectatorManager().spectate(player);
		}
	}

	//todo test: start a game when players is spectating
	public void unspectate(Player player) {
		getSpectatorManager().unspectate(player);
	}

	public boolean checkTeamEliminated(final Team team) {
		for (Member p : team.getMembers()) {
			if (!spectatorManager.getOmniSpectatorStorage().getSpectatorList().contains(p.getPlayer())) {
				return false;
			}
		}
		return true;
	}

	public void lockTeam(final Team team) {
		for (int i = 1; i <= (LOCK_TIME_IN_SECONDS * 10); i++) {
			task("ROUNDTASK_team-lock-" + team.getTeamId() + "_" + i, Bukkit.getScheduler().runTaskLater(plugin, () -> {
				teleportToLockPosition(team);
			}, i * 2L));

			if (i == 1 || i % 10 == 0) {
				task("ROUNDTASK_team-sendTitle-" + team.getTeamId() + "_" + i, Bukkit.getScheduler().runTaskLater(plugin, () -> {
					for (Member member : getAllMembers()) {
						plugin.getMessageAPI().sendTitle(member.getPlayer(), TitleHandler.getTitle(TitleHandler.TitleType.ROUND_STARTING,
								new PlaceholderUtil().add("{round}", roundData.getCurrentRound() + "")
										.add("{remaining-time-timer}", TimerUtils.formatTimerFormat(timer + 100))
										.add("{remaining-time-time}", TimerUtils.formatTimeFormat(timer + 100))));
					}
				}, i * 2L));
			}
		}
	}

	public void teleportToLockPosition(final Team team) {
		int i = 1;
		final Positions positions = arena.getPositions().get("spawn-team-" + team.getTeamId());
		if (positions == null) {
			Logger.severe("Player could not teleported to lock position because location set is null.");
			return;
		}
		for (final Member member : team.getMembers()) {
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

	public void cancelTasks(String prefix) {
		for (final Map.Entry<String, BukkitTask> task : tasks.entrySet()) {
			if (prefix == null || task.getKey().startsWith(prefix)) {
				task.getValue().cancel();
			}
		}
	}

	public GameBuilder getGameBuilder() {
		return gameBuilder;
	}

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
		if (gameState.equals(BATTLE)) {
			gameHistoryLog.setStartTime(System.currentTimeMillis());
		} else if (gameState.equals(GAME_ENDING)) {
			gameHistoryLog.finish(this);
			broadcastGameEnd();
		}
	}

	public RoundData getRoundData() {
		return roundData;
	}

	public GameSpectatorManager getSpectatorManager() {
		return spectatorManager;
	}

	public Arena getArena() {
		return arena;
	}

	public Kit getKit() {
		return kit;
	}

	public List<GameRule> getGameRules() {
		return gameRules;
	}

	public Map<Integer, Team> getTeams() {
		return teams;
	}

	public long getFinishTime() {
		return finishTime;
	}


	public Map<String, BukkitTask> getTasks() {
		return tasks;
	}

	public void task(final String taskName, final BukkitTask task) {
		final BukkitTask t = tasks.get(taskName);
		if (t != null) t.cancel();
		tasks.put(taskName, task);
	}

	public long getTimer() {
		return timer;
	}

	public MasterDuels getPlugin() {
		return plugin;
	}
}
