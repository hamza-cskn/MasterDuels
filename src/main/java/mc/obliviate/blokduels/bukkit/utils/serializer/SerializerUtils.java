package mc.obliviate.blokduels.bukkit.utils.serializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class SerializerUtils {
	public static void serializeLocation(final ConfigurationSection section, final Location location) {
		section.set("world", location.getWorld().getName());
		section.set("x", location.getX());
		section.set("y", location.getY());
		section.set("z", location.getZ());
		section.set("yaw", (double) location.getYaw());
		section.set("pitch", (double) location.getPitch());
	}

	public static Location deserializeLocation(final ConfigurationSection section) {
		if (section == null) return null;
		final World world = Bukkit.getWorld(section.getString("world"));
		if (world == null) {
			Bukkit.getLogger().severe("World couldn't found: " + section.getString("world"));
			return null;
		}

		final double x = section.getDouble("x");
		final double y = section.getDouble("y");
		final double z = section.getDouble("z");
		final double yaw = section.getDouble("yaw", 0);
		final double pitch = section.getDouble("pitch", 0);

		return new Location(world, x, y, z, (float) yaw, (float) pitch);

	}
}
