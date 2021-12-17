package mc.obliviate.blokduels.game;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.arena.elements.Positions;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.bossbar.BossBarData;
import mc.obliviate.blokduels.game.round.RoundData;
import mc.obliviate.blokduels.game.spectator.SpectatorStorage;
import mc.obliviate.blokduels.history.GameHistoryLog;
import mc.obliviate.blokduels.kit.InventoryStorer;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.user.Spectator;
import mc.obliviate.blokduels.user.User;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.user.team.Team;
import mc.obliviate.blokduels.utils.Logger;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.blokduels.utils.playerreset.PlayerReset;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import mc.obliviate.blokduels.utils.title.TitleHandler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static mc.obliviate.blokduels.data.DataHandler.LOCK_TIME_IN_SECONDS;
import static mc.obliviate.blokduels.game.GameState.*;

public class Game {

	private static int endDelay = 20;
	private final BlokDuels plugin;
	private final Arena arena;
	private final Map<Integer, Team> teams = new HashMap<>();
	private final Kit kit;
	private final long finishTime;
	private final List<GameRules> gameRules;
	private final List<Location> placedBlocks = new ArrayList<>();
	private final Map<String, BukkitTask> tasks = new HashMap<>();
	private final RoundData roundData = new RoundData();
	private final SpectatorStorage spectatorData = new SpectatorStorage(this);
	private final GameBuilder gameBuilder;
	private final BossBarData bossBarData = new BossBarData(this);
	private final GameHistoryLog gameHistoryLog = new GameHistoryLog();
	private long timer;
	private GameState gameState = GAME_STARING;
	//todo is this variables cloned with gamebuilder?

	protected Game(final BlokDuels plugin, final GameBuilder gameBuilder, final int totalRounds, final Arena arena, final Kit kit, final long finishTime, final List<GameRules> gameRules) {
		this.plugin = plugin;
		this.arena = arena;
		this.kit = kit;
		this.finishTime = finishTime;
		this.gameRules = gameRules;
		this.gameBuilder = gameBuilder;

		roundData.setTotalRounds(totalRounds);
		DataHandler.registerGame(arena, this);
	}

	public static int getEndDelay() {
		return endDelay;
	}

	public static void setEndDelay(final int endDelay) {
		Game.endDelay = endDelay;
	}

	public static GameBuilder create(BlokDuels plugin, Arena arena) {
		return new GameBuilder(plugin, arena);
	}

	public void addPlacedBlock(Location location) {
		placedBlocks.add(location);
	}

	public List<Location> getPlacedBlocks() {
		return placedBlocks;
	}

	protected void registerTeam(Team team) {
		teams.put(team.getTeamId(), team);
	}

	public void startGame() {
		broadcastInGame("game-started");
		updateScoreboardTasks();
		storeKits();
		nextRound();
	}

	public void initBossBar() {
		for (final Member member : getAllMembers()) {
			bossBarData.show(member);
		}
		bossBarData.init();
	}

	public void updateScoreboardTasks() {
		for (Member member : getAllMembers()) {
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
		if (!roundData.addRound()) {
			finishGame();
			return;
		}
		timer = System.currentTimeMillis() + (LOCK_TIME_IN_SECONDS * 1000L);
		setGameState(ROUND_STARTING);
		resetPlayers();
		reloadKits();
		lockTeams();
		spectatorData.unSpectateMembers();

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
			gameHistoryLog.setStartTime(System.currentTimeMillis());
		}
	}

	private void setFinishTimer() {
		timer = System.currentTimeMillis() + (finishTime * 1000);
		task("REMAINING_TIME", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			broadcastInGame("game-timed-out");
			uninstallGame();
		}, finishTime * 20));
	}

	public void storeKits() {
		for (final Member member : getAllMembers()) {
			if (!Kit.storeKits(member.getPlayer())) {
				Logger.severe(member.getPlayer().getName() + "'s inventory could not stored! Game cancelling.");
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
		setGameState(GAME_ENDING);
		cancelTasks("REMAINING_TIME");
		timer = endDelay * 1000L + System.currentTimeMillis();
		Bukkit.getScheduler().runTaskLater(plugin, this::uninstallGame, endDelay * 20L);


	}

	/**
	 * force to finish game method
	 */
	public void uninstallGame() {
		if (gameState.equals(UNINSTALLING)) {
			Logger.severe("Uninstall Game method called twice.");
			return;
		}

		setGameState(UNINSTALLING);
		broadcastInGame("game-finished");

		for (final Team team : teams.values()) {
			for (final Member member : team.getMembers()) {
				leave(member);
			}
		}

		cancelTasks(null);
		if (plugin.getDatabaseHandler().getConfig().getBoolean("arena-regeneration.enabled", true)) {
			clearArea();
		}
		DataHandler.registerArena(arena);
	}

	public void clearArea() {
		for (final Location loc : getPlacedBlocks()) {
			loc.getBlock().setType(Material.AIR, false);
		}
		if (plugin.getDatabaseHandler().getConfig().getBoolean("arena-regeneration.remove-entities", true)) {
			for (final Chunk chunk : arena.getArenaCuboid().getChunks()) {
				for (final Entity entity : chunk.getEntities()) {
					if (entity instanceof Item || entity instanceof Projectile) {
						entity.remove();
					}
				}
			}
		}
	}

	public void leave(final User user) {
		if (user instanceof Member) {
			leave((Member) user);
		} else if (user instanceof Spectator) {
			leave((Spectator) user);
		}
	}

	public void leave(final Spectator spectator) {
		spectatorData.unspectate(spectator);
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

		if (!InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("Inventory could not restored: " + member.getPlayer());
		}

		showAll(member.getPlayer());
		new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeGamemode().reset(member.getPlayer());
		ScoreboardManager.defaultScoreboard(member.getPlayer());


		if (DataHandler.getLobbyLocation() != null && !member.getPlayer().teleport(DataHandler.getLobbyLocation())) {
			if (!BlokDuels.isInShutdownMode()) {
				member.getPlayer().kickPlayer("You could not teleported to lobby.\n" + DataHandler.getLobbyLocation());
				Logger.error("Player " + member.getPlayer().getName() + " could not teleported to lobby. BlokDuels kicked him.");
			}
		}

		MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");

		if (member.getTeam().getMembers().size() == 0) {
			if (gameState.equals(GAME_ENDING) || gameState.equals(UNINSTALLING)) return;
			if (gameState.equals(BATTLE)) {
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

		for (final Player p : spectatorData.getSpectators()) {
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

	public void onDeath(final Member member, final Member attacker) {
		if (attacker == null) {
			broadcastInGame("player-dead.without-attacker", new PlaceholderUtil().add("{victim}", member.getPlayer().getName()));
		} else {
			broadcastInGame("player-dead.by-attacker", new PlaceholderUtil().add("{attacker}", attacker.getPlayer().getName()).add("{victim}", member.getPlayer().getName()));
		}
		spectatorData.spectate(member.getPlayer());
		if (checkTeamEliminated(member.getTeam())) {
			if (member.getTeam().getMembers().size() > 1) {
				broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", member.getPlayer().getName()));
			}
			nextRound();
		}
	}

	public void spectate(Player player) {
		if (DataHandler.getUser(player.getUniqueId()) == null) {
			getSpectatorData().spectate(player);
		}
	}

	public void unspectate(Player player) {
		getSpectatorData().unspectate(player);
	}

	public boolean checkTeamEliminated(final Team team) {
		for (Member p : team.getMembers()) {
			if (!spectatorData.getSpectators().contains(p.getPlayer())) {
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

	public GameBuilder getTeamBuilder() {
		return gameBuilder;
	}

	public GameState getGameState() {
		return gameState;
	}

	private void setGameState(GameState gameState) {
		this.gameState = gameState;
		onGameStateChange();
	}

	private void onGameStateChange() {
		if (gameState.equals(BATTLE)) {
			gameHistoryLog.setStartTime(System.currentTimeMillis());
		} else if (gameState.equals(GAME_ENDING)) {
			gameHistoryLog.setEndTime(System.currentTimeMillis());

			final List<UUID> losers = new ArrayList<>();
			final List<UUID> winners = new ArrayList<>();
			for (final Team team : getTeams().values()) {
				final List<UUID> list;

				if (checkTeamEliminated(team)) list = losers;
				else list = winners;

				for (final Member member : team.getMembers()) {
					list.add(member.getPlayer().getUniqueId());
				}
			}
			gameHistoryLog.setLosers(losers);
			gameHistoryLog.setWinners(winners);
			gameHistoryLog.save(plugin);
		}
	}

	public RoundData getRoundData() {
		return roundData;
	}

	public SpectatorStorage getSpectatorData() {
		return spectatorData;
	}

	public Arena getArena() {
		return arena;
	}

	public Kit getKit() {
		return kit;
	}

	public List<GameRules> getGameRules() {
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

	public BlokDuels getPlugin() {
		return plugin;
	}
}
