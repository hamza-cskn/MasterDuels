package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class ConfigurableGui extends Gui {

	public ConfigurableGui(Player player, String id) {
		super(player, id, "...", 0);
		setTitle(YamlStorageHandler.getSection(getSectionPath()).getString("title"));
		setSize(YamlStorageHandler.getSection(getSectionPath()).getInt("size") * 9);
	}

	abstract public String getSectionPath();

	public String getIconsSectionPath() {
		return getSectionPath() + ".icons";
	}

	public int getConfigSlot(String sectionName) {
		return GUISerializerUtils.getConfigSlot(YamlStorageHandler.getSection(getIconsSectionPath() + "." + sectionName));
	}

	public ItemStack getConfigItem(String sectionName) {
		return GUISerializerUtils.getConfigItem((YamlStorageHandler.getSection(getIconsSectionPath() + "." + sectionName)));
	}

	public ItemStack getConfigItem(String sectionName, PlaceholderUtil placeholderUtil) {
		return GUISerializerUtils.getConfigItem((YamlStorageHandler.getSection(getIconsSectionPath() + "." + sectionName)), placeholderUtil);
	}

	public void putDysfunctionalIcons() {
		GUISerializerUtils.putDysfunctionalIcons(this, YamlStorageHandler.getSection(getIconsSectionPath()));
	}

	public void putIcon(String configName, Consumer<InventoryClickEvent> click) {
		addItem(getConfigSlot(configName), new Icon(getConfigItem(configName)).onClick(click));
	}

	public void putIcon(String configName, PlaceholderUtil placeholderUtil, Consumer<InventoryClickEvent> click) {
		addItem(getConfigSlot(configName), new Icon(getConfigItem(configName, placeholderUtil)).onClick(click));
	}

}
