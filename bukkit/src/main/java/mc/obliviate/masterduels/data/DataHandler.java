package mc.obliviate.masterduels.data;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.user.team.Team;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataHandler {

	private static final Map<UUID, IUser> users = new HashMap<>();
	private static final Map<Arena, Game> arenas = new HashMap<>();
	public static int LOCK_TIME_IN_SECONDS = 3;
	private static Location lobbyLocation = null;

	public static ITeam getTeam(final UUID uuid) {
		final IMember member = getMember(uuid);
		if (member == null) return null;
		return member.getTeam();
	}

	public static IGame getGame(final UUID uuid) {
		final ITeam team = getTeam(uuid);
		if (team == null) return null;
		return team.getGame();
	}

	public static IMember getMember(final UUID uuid) {
		final IUser user = users.get(uuid);
		if (user instanceof IMember) {
			return (IMember) user;
		}
		return null;
	}

	public static ISpectator getSpectator(final UUID uuid) {
		final IUser user = users.get(uuid);
		if (user instanceof ISpectator) {
			return (ISpectator) user;
		}
		return null;
	}

	public static IUser getUser(final UUID uuid) {
		return users.get(uuid);
	}

	public static Map<UUID, IUser> getUsers() {
		return users;
	}

	public static Map<Arena, Game> getArenas() {
		return arenas;
	}

	public static void registerArena(final Arena arena) {
		if (arena == null) return;
		arenas.put(arena, null);
	}

	public static void registerGame(final Arena arena, final Game game) {
		if (arenas.containsKey(arena)) {
			//put if value is null,
			arenas.putIfAbsent(arena, game);
		}
	}

	public static Location getLobbyLocation() {
		return lobbyLocation;
	}

	public static void setLobbyLocation(Location lobbyLocation) {
		if (lobbyLocation == null) return;
		if (lobbyLocation.getWorld() == null) return;
		DataHandler.lobbyLocation = lobbyLocation;
	}

	public static Arena getArenaFromName(String arenaName) {
		for (final Arena arena : arenas.keySet()) {
			if (arena != null && arena.getName() != null && arena.getName().equalsIgnoreCase(arenaName)) {
				return arena;
			}
		}
		return null;
	}
}
