package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GUISerializerUtils {

	public static void putDysfunctionalIcons(Gui gui, ConfigurationSection iconsSection, PlaceholderUtil placeholderUtil) {
		if (iconsSection == null) throw new IllegalArgumentException("null configuration section given!");
		for (String sectionName : iconsSection.getKeys(false)) {
			final ConfigurationSection section = iconsSection.getConfigurationSection(sectionName);

			if (!section.isSet("slot")) continue;
			final int slotNo = section.getInt("slot", -1);
			if (slotNo != -1) {
				gui.addItem(slotNo, getConfigItem(iconsSection.getConfigurationSection(sectionName), placeholderUtil));
				continue;
			}

			final String slotString = section.getString("slot", "");
			if (slotString.contains("-")) {
				final String[] slots = slotString.split("-");
				if (slots.length != 2) continue;
				int from, to;
				try {
					from = Integer.parseInt(slots[0]);
					to = Integer.parseInt(slots[1]);
				} catch (NumberFormatException e) {
					continue;
				}
				if (from > to) continue;
				for (; from <= to; from++) {
					gui.addItem(from, getConfigItem(iconsSection.getConfigurationSection(sectionName), placeholderUtil));
				}
				continue;
			} else if (slotString.contains(",")) {
				final String[] slots = slotString.split(",");
				if (slots.length < 2) continue;

				for (final String slotText : slots) {
					try {
						gui.addItem(Integer.parseInt(slotText), getConfigItem(iconsSection.getConfigurationSection(sectionName), placeholderUtil));
					} catch (NumberFormatException ignore) {
					}
				}
			}
		}
	}

	public static ItemStack getConfigItem(ConfigurationSection section) {
		return SerializerUtils.deserializeItemStack(section, null);
	}

	public static ItemStack getConfigItem(ConfigurationSection section, PlaceholderUtil placeholderUtil) {
		return SerializerUtils.deserializeItemStack(section, placeholderUtil);
	}

	public static int getConfigSlot(ConfigurationSection section) {
		return section.getInt("slot");
	}


}
