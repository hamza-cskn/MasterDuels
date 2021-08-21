package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.team.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataHandler {

	private static final Map<UUID, Member> members = new HashMap<>();
	private static final Map<Arena, Game> arenas = new HashMap<>();
	public static int LOCK_TIME_IN_SECONDS = 3;

	public static Team getTeam(final UUID uuid) {
		final Member member = members.get(uuid);
		if (member == null) return null;
		return member.getTeam();
	}

	public static Game getGame(final UUID uuid) {
		final Team team = getTeam(uuid);
		if (team == null) return null;
		return team.getGame();
	}

	public static Member getMember(final UUID uuid) {
		return members.get(uuid);
	}

	public static Map<UUID, Member> getMembers() {
		return members;
	}

	public static Map<Arena, Game> getArenas() {
		return arenas;
	}

	public static void registerArena(final Arena arena) {
		arenas.put(arena, null);
	}

	public static void registerGame(final Arena arena, final Game game) {
		if (arenas.containsKey(arena)) {
			//put if value is null,
			arenas.putIfAbsent(arena, game);
		}
	}
}
