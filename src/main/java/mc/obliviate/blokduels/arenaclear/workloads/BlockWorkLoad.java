package mc.obliviate.blokduels.arenaclear.workloads;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class BlockWorkLoad implements IWorkLoad {

	private final int x;
	private final int y;
	private final int z;
	private final UUID worldUID;

	public BlockWorkLoad(final int x, final int y, final int z, final UUID worldUID) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldUID = worldUID;
	}

	@Override
	public void compute(Plugin plugin) {
		final Block block = getBlock();
		if (block == null) return;
		if (block.getType() == null) return;
		if (block.getType().equals(Material.AIR)) return;
		switch (block.getType()) {
			case STATIONARY_LAVA:
			case STATIONARY_WATER:
			case WATER:
			case LAVA:
				new LiquidWorkload(x, y, z, worldUID).compute(plugin);
				return;
		}
		block.removeMetadata("placedByPlayer", plugin);
		block.setType(Material.AIR, false);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public UUID getWorldUID() {
		return worldUID;
	}

}
