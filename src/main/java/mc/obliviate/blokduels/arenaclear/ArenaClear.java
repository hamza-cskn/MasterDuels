package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.arenaclear.workloads.BlockWorkLoad;
import mc.obliviate.blokduels.arenaclear.workloads.LiquidWorkload;
import mc.obliviate.blokduels.arenaclear.workloads.WorkLoadThread;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;

import java.util.UUID;

public class ArenaClear {

	public static boolean removeEntities = true;
	private final WorkLoadThread thread;
	private final Arena arena;

	public ArenaClear(BlokDuels plugin, Arena arena) {
		this.thread = new WorkLoadThread(plugin);
		this.arena = arena;
	}

	public void addBlock(int x, int y, int z, UUID worldUID) {
		thread.addWorkLoad(new BlockWorkLoad(x, y, z, worldUID));
	}

	public void addLiquid(int x, int y, int z, UUID worldUID) {
		thread.addWorkLoad(new LiquidWorkload(x, y, z, worldUID));
	}


	public void clear() {
		thread.run();
		if (removeEntities) {
			clearEntities();
		}
	}

	public void clearEntities() {
		for (final Chunk chunk : arena.getArenaCuboid().getChunks()) {
			for (final Entity entity : chunk.getEntities()) {
				if (entity instanceof Item || entity instanceof Projectile) {
					entity.remove();
				}
			}
		}

	}

}
