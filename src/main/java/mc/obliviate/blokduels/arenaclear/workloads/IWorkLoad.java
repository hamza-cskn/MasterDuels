package mc.obliviate.blokduels.arenaclear.workloads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public interface IWorkLoad {

	void compute(Plugin plugin);

	int getX();

	int getY();

	int getZ();

	UUID getWorldUID();

	default Block getBlock() {
		return new Location(Bukkit.getWorld(getWorldUID()), getX(), getY(), getZ()).getBlock();
	}
	default boolean equals(int x, int y, int z, UUID world) {
		if (getX() != x) return false;
		if (getY() != y) return false;
		if (getZ() != z) return false;
		if (getWorldUID() != world) return false;
		return true;
	}
}
