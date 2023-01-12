package mc.obliviate.masterduels.utils.optimization;

import mc.obliviate.masterduels.arena.Arena;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaWorldOptimizerHandler {

	private final Map<UUID, WorldOptimizer> worlds = new HashMap<>();

	public void init() {
		for (final Arena arena : Arena.getArenasMap().keySet()) {

			final UUID worldUniqueId = arena.getArenaCuboid().getPoint1().getWorld().getUID();
			if (worlds.containsKey(worldUniqueId)) continue;

			worlds.put(worldUniqueId, new WorldOptimizer(worldUniqueId));
		}
	}

	public Map<UUID, WorldOptimizer> getArenaWorlds() {
		return worlds;
	}
}


