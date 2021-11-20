package mc.obliviate.blokduels.kit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class Kit {

	private static final Map<String, Kit> kits = new HashMap<>();
	private final PlayerInventoryFrame playerInventoryFrame;
	private final String kitName;

	public Kit() {
		this(null, null, null);
	}

	public Kit(String name, ItemStack[] contents, ItemStack[] armorContents) {
		playerInventoryFrame = new PlayerInventoryFrame(contents, armorContents);
		kitName = name;
		kits.put(name, this);
	}

	public static Map<String, Kit> getKits() {
		return kits;
	}

	public static boolean storeKits(final Player player) {
		return InventoryStorer.store(player);
	}

	public static void reload(final Kit kit, final Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		if (kit == null || kit.getContents() == null) return;
		final PlayerInventory inv = player.getInventory();
		inv.setContents(kit.getContents());
		inv.setArmorContents(kit.getArmorContents());
		player.updateInventory();
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
}
