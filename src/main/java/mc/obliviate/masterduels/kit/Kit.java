package mc.obliviate.masterduels.kit;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Kit {

	public static boolean USE_PLAYER_INVENTORIES = false;
	private static final Map<String, Kit> kits = new HashMap<>();
	private final PlayerInventoryFrame playerInventoryFrame;
	private final String kitName;
	private static final String kitFileName = "kits.yml";
	private ItemStack icon;

	public Kit(String name, ItemStack[] contents, ItemStack[] armorContents) {
		this(name, contents, armorContents, new ItemStack(Material.DIAMOND_CHESTPLATE));
	}

	public Kit(String name, ItemStack[] contents, ItemStack[] armorContents, ItemStack icon) {
		playerInventoryFrame = new PlayerInventoryFrame(contents, armorContents);
		kitName = name;
		kits.put(name, this);
		this.icon = icon;
	}

	public static Map<String, Kit> getKits() {
		return kits;
	}

	public static boolean storeKits(final Player player) {
		if (USE_PLAYER_INVENTORIES) return true;
		return InventoryStorer.store(player) != null;
	}

	public static void load(final Kit kit, final Player player) {
		if (USE_PLAYER_INVENTORIES) return;
		if (kit == null) return;
		PlayerInventoryFrame.loadInventoryFrame(player, kit.playerInventoryFrame);
	}

	public static void save(MasterDuels plugin, Kit kit) {
		final File file = new File(plugin.getDataFolder().getPath() + File.separator + kitFileName);
		final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

		KitSerializer.serialize(kit, data.createSection(kit.getKitName()));
		try {
			data.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ItemStack[] getArmorContents() {
		return playerInventoryFrame.getArmorContents();
	}

	public ItemStack[] getContents() {
		return playerInventoryFrame.getContents();
	}

	public String getKitName() {
		return kitName;
	}

	public ItemStack getIcon() {
		if (icon == null || icon.getType().equals(Material.AIR)) return new ItemStack(Material.DIAMOND_CHESTPLATE);
		return icon;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}
}
