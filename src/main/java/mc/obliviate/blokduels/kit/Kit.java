package mc.obliviate.blokduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		return InventoryStorer.store(player) != null;
	}

	public static void load(final Kit kit, final Player player) {
		if (kit == null) return;
		PlayerInventoryFrame.loadInventoryFrame(player, kit.playerInventoryFrame);
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
