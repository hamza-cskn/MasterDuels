package mc.obliviate.masterduels.utils.serializer;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SerializerUtils {

	// START_TIME;GAME_TIME;UUID1,UUID2,UUID3 | START_TIME;GAME_TIME;UUID1,UUID2,UUID3
	public static final String ELEMENT_SPLIT_CHARACTER = ",";
	public static final String OBJECT_SPLIT_CHARACTER = ";";
	public static final String DATA_SPLIT_CHARACTER = "|";

	public static void serializeLocationYAML(final ConfigurationSection section, final Location location) {
		if (location == null) return;
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

	public static void serializeIcon(final ConfigurationSection serializedSection, final int slot, final Icon item) {
		final ItemMeta meta = item.getItem().getItemMeta();

		setSafe(serializedSection, "material", XMaterial.matchXMaterial(item.getItem()).parseMaterial().toString());
		setSafe(serializedSection, "slot", slot);
		if (meta != null) {
			setSafe(serializedSection, "display-name", meta.getDisplayName().replace("§", "&"));
			if (meta.getLore() != null && meta.getLore().size() > 0) {
				final List<String> list = new ArrayList<>();
				meta.getLore().forEach(line -> list.add(line.replace("§", "&")));
				setSafe(serializedSection, "lore", list);
			}
			//setSafe(serializedSection, "custom-model-data", meta.getCustomModelData());
			setSafe(serializedSection, "item-flags", Collections.singletonList(meta.getItemFlags()));
		}
		setSafe(serializedSection, "amount", item.getItem().getAmount());

		for (final Map.Entry<Enchantment, Integer> enchant : item.getItem().getEnchantments().entrySet()) {
			setSafe(serializedSection, "enchantments." + enchant.getKey(), enchant.getValue());
		}
	}

	public static ItemStack deserializeItemStack(ConfigurationSection section, PlaceholderUtil placeholderUtil) {
		final Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(section.getString("material", "BEDROCK"));
		if (!xmaterial.isPresent()) {
			Logger.error("Material could not found: " + section.getString("material"));
			return null;
		}

		final ItemStack item = xmaterial.get().parseItem();
		if (item == null) {
			Logger.error("Material could not parsed as itemstack: " + section.getString("material"));
			return null;
		}

		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageUtils.parseColor(MessageUtils.applyPlaceholders(section.getString("display-name"), placeholderUtil)));
		meta.setLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(section.getStringList("lore"), placeholderUtil)));
		item.setItemMeta(meta);
		item.setAmount(section.getInt("amount", 1));
		return item;
	}

	private static void setSafe(ConfigurationSection section, String key, Object value) {
		if (value != null) {
			if (value instanceof List && ((List) value).size() == 0) {
				return;
			}
			section.set(key, value);
		}
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
