package mc.obliviate.masterduels.utils.optimization;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

public class WorldOptimizer {

	private final UUID worldUniqueId;

	public WorldOptimizer(UUID worldUniqueId) {
		this.worldUniqueId = worldUniqueId;
		resetOptimizations();
	}

	public void resetOptimizations() {
		final World world = getWorld();
		world.setMonsterSpawnLimit(0);
		world.setAnimalSpawnLimit(0);
		world.setAmbientSpawnLimit(0);
		world.setWaterAnimalSpawnLimit(0);
		world.setThundering(false);
		world.setStorm(false);
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("randomTickSpeed", "0");
		world.setGameRuleValue("doMobSpawning", "false");
		//todo test on 1.18
	}

	public UUID getWorldUniqueId() {
		return worldUniqueId;
	}
	public World getWorld() {
		return Bukkit.getWorld(worldUniqueId);
	}
}
