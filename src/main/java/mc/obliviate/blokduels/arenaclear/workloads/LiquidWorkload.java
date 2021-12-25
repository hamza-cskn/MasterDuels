package mc.obliviate.blokduels.arenaclear.workloads;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.UUID;

public class LiquidWorkload implements IWorkLoad {

	private final int x;
	private final int y;
	private final int z;
	private final UUID worldUID;

	public LiquidWorkload(final int x, final int y, final int z, final UUID worldUID) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldUID = worldUID;
	}

	@Override
	public void compute() {
		final Block block = getBlock();
		if (block == null) return;
		if (block.getType() == null) return;
		if (block.getType().equals(Material.AIR)) return;

		block.setType(Material.SPONGE, true);
		block.setType(Material.AIR, true);
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
