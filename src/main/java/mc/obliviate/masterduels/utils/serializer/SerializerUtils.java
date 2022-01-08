package mc.obliviate.masterduels.utils.serializer;

import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SerializerUtils {

	// START_TIME;GAME_TIME;UUID1,UUID2,UUID3 | START_TIME;GAME_TIME;UUID1,UUID2,UUID3
	public static final String ELEMENT_SPLIT_CHARACTER = ",";
	public static final String OBJECT_SPLIT_CHARACTER = ";";
	public static final String DATA_SPLIT_CHARACTER = "|";

	public static void serializeLocationYAML(final ConfigurationSection section, final Location location) {
		section.set("world", location.getWorld().getName());
		section.set("x", location.getX());
		section.set("y", location.getY());
		section.set("z", location.getZ());
		section.set("yaw", (double) location.getYaw());
		section.set("pitch", (double) location.getPitch());
	}

	public static Location deserializeLocationYAML(final ConfigurationSection section) {
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

	public static String serializeStringConvertableList(final List<?> list) {
		final StringBuilder builder = new StringBuilder();
		int i = 0;
		for (final Object o : list) {
			builder.append(o.toString());
			if (++i != list.size()) {
				builder.append(ELEMENT_SPLIT_CHARACTER);
			}
		}
		return builder.toString();
	}

	public static List<UUID> deserializeUUIDList(String serializedString) {
		if (serializedString.isEmpty()) return new ArrayList<>();
		final List<UUID> list = new ArrayList<>();
		for (String uuidString : serializedString.split(ELEMENT_SPLIT_CHARACTER)) {
			try {
				list.add(UUID.fromString(uuidString));
			} catch (IllegalArgumentException e) {
				Logger.error("String can not deserialized as UUID: " + uuidString);
			}
		}

		return list;
	}

	public static GameHistoryLog deserializeGameHistoryLog(ResultSet rs) throws SQLException {
		final String uuid = rs.getString("uuid");
		final String serializedWinners = rs.getString("winners");
		final String serializedLosers = rs.getString("losers");
		final long startTime = rs.getLong("startTime");
		final long endTime = rs.getLong("endTime");

		return new GameHistoryLog(UUID.fromString(uuid), startTime, endTime, SerializerUtils.deserializeUUIDList(serializedWinners), SerializerUtils.deserializeUUIDList(serializedLosers));

	}

}
