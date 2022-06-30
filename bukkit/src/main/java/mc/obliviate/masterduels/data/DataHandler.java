package mc.obliviate.masterduels.data;

import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.Match;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class DataHandler {

	private static final Map<Arena, Match> arenas = new HashMap<>();
	private static Location lobbyLocation = null;

	public static Map<Arena, Match> getArenas() {
		return arenas;
	}

	public static void registerArena(final Arena arena) {
		if (arena == null) return;
		arenas.put(arena, null);
	}

	public static void registerGame(final Arena arena, final Match game) {
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
