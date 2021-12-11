package mc.obliviate.blokduels.kit.serializer;

import mc.obliviate.blokduels.kit.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitSerializer {

	public static void serialize(Kit kit, ConfigurationSection section) {
		serializeItemList(section.createSection("items"), kit.getContents());
		serializeItemList(section.createSection("armors"), kit.getArmorContents());

	}

	private static void serializeItemList(ConfigurationSection section, ItemStack[] items) {
		int i = -1;
		for (ItemStack item : items) {
			i++;
			if (item == null) continue;
			section.set(i + "", item);
		}
	}

	private static ItemStack[] deserializeItemList(ConfigurationSection section, int size) {
		if (section == null)
			throw new IllegalArgumentException("section could not deserialized because section was null.");
		final Map<Integer, ItemStack> result = new HashMap<>();
		for (String key : section.getKeys(false)) {
			result.put(Integer.parseInt(key), section.getItemStack(key));
		}

		final ItemStack[] items = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			items[i] = result.get(i);
		}

		return items;
	}

	public static Kit deserialize(ConfigurationSection section) {
		final ItemStack[] items = deserializeItemList(section.getConfigurationSection("items"), 36);
		final ItemStack[] armors = deserializeItemList(section.getConfigurationSection("armors"), 4);
		return new Kit(section.getName(), items, armors);
	}

}
