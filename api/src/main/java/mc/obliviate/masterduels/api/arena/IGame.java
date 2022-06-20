package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.arena.spectator.IGameSpectatorManager;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.api.user.IUser;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;

public interface IGame {

	void startGame();

	void initBossBar();

	void updateScoreboardTasks();

	void nextRound();

	void onRoundStart(final int round);

	void storeKits();

	void reloadKits();

	void finishRound();

	List<IMember> getAllMembers();

	List<Player> getAllMembersAndSpectatorsAsPlayer();

	/**
	 * natural finish game method
	 */
	void finishGame();

	/**
	 * ultimate finish game method
	 */
	void uninstallGame();

	void dropItems(final Player player);

	void leave(final Player player);

	void leave(final IUser user);

	void leave(final ISpectator spectator);

	void leave(final IMember member);

	boolean isMember(Player player);

	void resetPlayers();

	void lockTeams();

	void onDeath(final IMember victim, final IMember attacker);

	void spectate(Player player);

	//todo test: start a game when player is spectating
	void unspectate(Player player);

	boolean checkTeamEliminated(final ITeam team);

	void lockTeam(final ITeam team);

	void teleportToLockPosition(final ITeam team);

	void cancelTasks(String prefix);

	IGameBuilder getGameBuilder();

	GameState getGameState();

	IRoundData getRoundData();

	IGameSpectatorManager getSpectatorManager();

	IArena getArena();

	IKit getKit();

	List<GameRule> getGameRules();

	Map<Integer, ITeam> getTeams();

	long getFinishTime();

	Map<String, BukkitTask> getTasks();

	long getTimer();

}


