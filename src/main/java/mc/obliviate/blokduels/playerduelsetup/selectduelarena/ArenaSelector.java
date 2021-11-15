package mc.obliviate.blokduels.playerduelsetup.selectduelarena;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.utils.MessageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaSelector {

	private static Map<String, ArenaSelector> arenaSelectors = new HashMap<>();

	private final List<Arena> arenas = new ArrayList<>();
	private final int teamSize;
	private final int teamAmount;

	private ArenaSelector(int teamSize, int teamAmount) {
		this.teamSize = teamSize;
		this.teamAmount = teamAmount;
		arenaSelectors.put(getModeFormat(), this);
	}

	public static void calculate() {
		arenaSelectors.clear();
		for (final Arena arena : DataHandler.getArenas().keySet()) {

			final Game game = DataHandler.getArenas().get(arena);
			if (game != null) continue;

			final String converetedMode = MessageUtils.convertMode(arena.getTeamSize(), arena.getTeamAmount());
			ArenaSelector arenaSelector = arenaSelectors.get(converetedMode);
			if (arenaSelector == null) {
				arenaSelector = new ArenaSelector(arena.getTeamSize(), arena.getTeamAmount());
				arenaSelectors.put(converetedMode, arenaSelector);
			}
			arenaSelector.addArena(arena);

		}
	}

	public static Map<String, ArenaSelector> getArenaSelectors() {
		return arenaSelectors;
	}

	public void addArena(Arena arena) {
		arenas.add(arena);
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public List<Arena> getArenas() {
		return arenas;
	}

	public String getModeFormat() {
		return MessageUtils.convertMode(teamSize, teamAmount);
	}


}
