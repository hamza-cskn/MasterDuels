package mc.obliviate.masterduels.data;

import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.user.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataHandler {

	private static final Map<UUID, IUser> users = new HashMap<>();
	private static final Map<Arena, Game> arenas = new HashMap<>();
	public static int LOCK_TIME_IN_SECONDS = 3;
	private static Location lobbyLocation = null;

	public static Team getTeam(final UUID uuid) {
		final Member member = getMember(uuid);
		if (member == null) return null;
		return member.getTeam();
	}

	public static Game getGame(final UUID uuid) {
		final Team team = getTeam(uuid);
		if (team == null) return null;
		return team.getGame();
	}

	public static Member getMember(final UUID uuid) {
		final IUser user = users.get(uuid);
		if (user instanceof Member) {
			return (Member) user;
		}
		return null;
	}

	public static Spectator getSpectator(final UUID uuid) {
		final IUser user = users.get(uuid);
		if (user instanceof Spectator) {
			return (Spectator) user;
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
		if (Bukkit.getWorld(lobbyLocation.getWorld().getUID()) != null) return;
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
