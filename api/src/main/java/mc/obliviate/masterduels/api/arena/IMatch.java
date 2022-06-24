package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.arena.spectator.IMatchSpectatorManager;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IMatch {

	void start();

	IMember getMember(UUID playerUniqueId);

	/**
	 * adds player to game
	 *
	 * @param player the player
	 * @param kit    kit of the player
	 * @param teamNo team no of player
	 */
	void addPlayer(Player player, IKit kit, int teamNo);

	/**
	 * removes player from game
	 * if game doesn't contains param player
	 * the method would ignores it.
	 *
	 * @param playerUniqueId
	 */
	void removePlayer(UUID playerUniqueId);

	IMatchTaskManager getGameTaskManager();

	IMatchSpectatorManager getGameSpectatorManager();

	IMatchDataStorage getGameDataStorage();

	IArena getArena();

	IMatchState getMatchState();

	List<IMember> getAllMembers();

	List<Player> getAllMembersAndSpectatorsAsPlayer();

	/**
	 * uninstalls match instantly.
	 * unregisters match, members, spectators; clears arena etc...
	 */
	void uninstall();

	/**
	 * finishes match naturally.
	 */
	void finish();

	/**
	 * Shows every player to param player
	 *
	 * @param player
	 */
	//todo vanish plugin compatibility
	void showAll(Player player);

	void dropItems(final Player player, Location loc);

	void resetPlayers();

}


