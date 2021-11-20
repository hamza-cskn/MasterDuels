package mc.obliviate.blokduels.game;

import com.hakan.messageapi.bukkit.title.Title;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.arena.elements.Positions;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.round.RoundData;
import mc.obliviate.blokduels.game.spectator.SpectatorData;
import mc.obliviate.blokduels.kit.InventoryStorer;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.team.Team;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.blokduels.utils.playerreset.PlayerReset;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mc.obliviate.blokduels.data.DataHandler.LOCK_TIME_IN_SECONDS;
import static mc.obliviate.blokduels.game.GameState.*;

public class Game {

	private final BlokDuels plugin;
	private final Arena arena;
	private final Map<Integer, Team> teams = new HashMap<>();
	private final Kit kit;
	private final long finishTime;
	private final List<GameRules> gameRules;
	private final List<Location> placedBlocks = new ArrayList<>();
	private final Map<String, BukkitTask> tasks = new HashMap<>();
	private final RoundData roundData = new RoundData();
	private final SpectatorData spectatorData = new SpectatorData();
	private final GameBuilder gameBuilder;
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
		broadcastInGame("game-has-started");
		updateScoreboardTasks();
		storeKits();
		nextRound();
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
		gameState = ROUND_STARTING;
		resetPlayers();
		reloadKits();
		lockTeams();
		unSpectateMembers();

		task("ROUNDTASK_on-round-start-timer", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			gameState = BATTLE;
			onRoundStart(roundData.getCurrentRound());
		}, LOCK_TIME_IN_SECONDS * 20L + 1));


	}

	public void onRoundStart(final int round) {
		broadcastInGame("round-has-started", new PlaceholderUtil().add("{round}", round + ""));
		updateScoreboardTasks();
		setFinishTimer();
	}

	//todo not tested
	private void setFinishTimer() {
		timer = System.currentTimeMillis() + (finishTime * 1000);
		task("REMAINING_TIME", Bukkit.getScheduler().runTaskLater(plugin, () -> {
			broadcastInGame("game-has-timed-out");
			finishGame();
		}, finishTime * 20));
	}

	public void storeKits() {
		for (final Member member : getAllMembers()) {
			if (!Kit.storeKits(member.getPlayer())) {
				Bukkit.getLogger().severe("[BlokDuels] " + member.getPlayer().getName() + "'s inventory could not stored! Game cancelling.");
				cancelGame();
			}

		}
	}

	public void reloadKits() {
		for (final Member member : getAllMembers()) {
			Kit.reload(kit, member.getPlayer());
		}
	}

	public void finishRound() {
		cancelRoundTasks();
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

	public void finishGame() {
		broadcastInGame("game-has-finished");

		gameState = GAME_ENDING;
		cancelAllTasks();
		for (Team team : teams.values()) {
			for (Member member : team.getMembers()) {
				leaveMember(member);
			}
		}

		clearArea();
		DataHandler.registerArena(arena);
	}

	public void clearArea() {
		for (final Location loc : getPlacedBlocks()) {
			loc.getBlock().setType(Material.AIR);
		}
	}

	public void leaveMember(final Member member) {
		DataHandler.getMembers().remove(member.getPlayer().getUniqueId());
		member.getTeam().removeMember(member);

		showAll(member.getPlayer());
		if (!InventoryStorer.restore(member.getPlayer())) {
			Bukkit.getLogger().severe("[BlokDuels] inventory couldn't restored: " + member.getPlayer());
		}

		if (DataHandler.getLobbyLocation() != null && !member.getPlayer().teleport(DataHandler.getLobbyLocation())) {
			member.getPlayer().kickPlayer("You could not teleported to lobby.");
		}

		MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeGamemode().excludeTitle().reset(member.getPlayer());
		ScoreboardManager.defaultScoreboard(member.getPlayer());

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

	public void cancelGame() {
		finishGame();
	}

	public void resetPlayers() {
		final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
		for (final Member member : getAllMembers()) {
			playerReset.reset(member.getPlayer());
		}
	}

	private void unSpectateMembers() {
		final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
		for (final Member member : getAllMembers()) {
			if (spectatorData.isSpectator(member.getPlayer())) {
				spectatorData.remove(member.getPlayer());
			}
		}

	}

	public void lockTeams() {
		for (final Team team : teams.values()) {
			lockTeam(team);
		}
	}

	public void onDeath(final Member member) {
		broadcastInGame("duel-player-death", new PlaceholderUtil().add("{victim}", member.getPlayer().getName()));
		makeSpectator(member);
		if (checkTeamEliminated(member.getTeam())) {
			broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", member.getPlayer().getName()));
			nextRound();
		}
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
						plugin.getMessageAPI().sendTitle(member.getPlayer(), new Title(ChatColor.YELLOW + "Raund başlıyor", ChatColor.RED + TimerUtils.convertTimer(timer + 100), 15, 0, 5));
					}
				}, i * 2L));
			}
		}
	}

	public void teleportToLockPosition(final Team team) {
		int i = 1;
		final Positions positions = arena.getPositions().get("spawn-team-" + team.getTeamId());
		if (positions == null) {
			Bukkit.broadcastMessage("positions == null");
			return;
		}
		for (final Member member : team.getMembers()) {
			final Location loc = positions.getLocation(i++);
			if (loc == null) {
				Bukkit.broadcastMessage("location is null");
			}
			if (!member.getPlayer().teleport(loc)) {
				Bukkit.getLogger().severe("[BlokDuels] [ERROR] Player " + member.getPlayer().getName() + " could not teleported to duel arena.");
				cancelGame();
			}

		}
	}

	public void cancelAllTasks() {
		for (final BukkitTask task : tasks.values()) {
			task.cancel();
		}
	}

	public void cancelRoundTasks() {
		for (final Map.Entry<String, BukkitTask> task : tasks.entrySet()) {
			if (task.getKey().startsWith("ROUNDTASK_")) {
				task.getValue().cancel();
			}
		}
	}

	public void cancelScoreboardTasks() {
		for (final Map.Entry<String, BukkitTask> task : tasks.entrySet()) {
			if (task.getKey().startsWith("SCOREBOARDTASK_")) {
				task.getValue().cancel();
			}
		}
	}

	public void makeSpectator(final Member member) {
		final Player player = member.getPlayer();

		new PlayerReset().excludeGamemode().excludeLevel().excludeExp().reset(player);

		for (final Team team : teams.values()) {
			for (final Member m : team.getMembers()) {
				m.getPlayer().hidePlayer(player);
			}
		}

		for (final Player spectator : spectatorData.getSpectators()) {
			spectator.showPlayer(player);
			player.showPlayer(spectator);
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		spectatorData.add(player);
		MessageUtils.sendMessage(member.getPlayer(), "you-are-a-spectator");
	}

	public GameBuilder getTeamBuilder() {
		return gameBuilder;
	}

	public GameState getGameState() {
		return gameState;
	}

	public RoundData getRoundData() {
		return roundData;
	}

	public SpectatorData getSpectatorData() {
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
}
