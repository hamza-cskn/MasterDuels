package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigurableGui extends Gui {

	public ConfigurableGui(Player player, String id) {
		super(player, id, "...", 0);
		setTitle(ConfigurationHandler.getMenusSection(getSectionPath()).getString("title"));
		setSize(ConfigurationHandler.getMenusSection(getSectionPath()).getInt("size") * 9);
	}

	abstract public String getSectionPath();

	public String getIconsSectionPath() {
		return getSectionPath() + ".icons";
	}

	public ConfigurationSection getIconsSection(String sectionName) {
		return ConfigurationHandler.getMenusSection(getIconsSectionPath() + "." + sectionName);
	}

	public int getConfigSlot(String sectionName) {
		return GUISerializerUtils.getConfigSlot(getIconsSection(sectionName));
	}

	public ItemStack getConfigItem(String sectionName) {
		return GUISerializerUtils.getConfigItem((getIconsSection(sectionName)));
	}

	public ItemStack getConfigItem(String sectionName, PlaceholderUtil placeholderUtil) {
		return GUISerializerUtils.getConfigItem(getIconsSection(sectionName), placeholderUtil);
	}

	public void putDysfunctionalIcons() {
		GUISerializerUtils.putDysfunctionalIcons(this, ConfigurationHandler.getMenusSection(getIconsSectionPath()), null, new ArrayList<>());
	}

	public void putDysfunctionalIcons(PlaceholderUtil placeholderUtil) {
		GUISerializerUtils.putDysfunctionalIcons(this, ConfigurationHandler.getMenusSection(getIconsSectionPath()), placeholderUtil, new ArrayList<>());
	}

	public void putDysfunctionalIcons(List<String> functionalSlots) {
		GUISerializerUtils.putDysfunctionalIcons(this, ConfigurationHandler.getMenusSection(getIconsSectionPath()), null, functionalSlots);
	}

	public void putDysfunctionalIcons(PlaceholderUtil placeholderUtil, List<String> functionalSlots) {
		GUISerializerUtils.putDysfunctionalIcons(this, ConfigurationHandler.getMenusSection(getIconsSectionPath()), placeholderUtil, functionalSlots);
	}

	public void putIcon(String configName, Consumer<InventoryClickEvent> click) {
		addItem(getConfigSlot(configName), new Icon(getConfigItem(configName)).hideFlags().onClick(click));
	}

	public void putIcon(String configName, PlaceholderUtil placeholderUtil, Consumer<InventoryClickEvent> click) {
		addItem(getConfigSlot(configName), new Icon(getConfigItem(configName, placeholderUtil)).hideFlags().onClick(click));
	}

}
