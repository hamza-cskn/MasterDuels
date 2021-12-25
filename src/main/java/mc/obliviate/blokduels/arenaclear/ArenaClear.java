package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;

public class ArenaClear {

	public final Arena arena;
	private final BlokDuels plugin;
	public final WorkLoadThread workLoadThread;

	public ArenaClear(Arena arena, BlokDuels plugin) {
		this.arena = arena;
		this.plugin = plugin;
		workLoadThread = new WorkLoadThread(plugin);
	}

	public void clear() {
	}

	public Arena getArena() {
		return arena;
	}

	public WorkLoadThread getWorkLoadThread() {
		return workLoadThread;
	}
}
