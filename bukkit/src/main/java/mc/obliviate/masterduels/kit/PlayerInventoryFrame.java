package mc.obliviate.masterduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryFrame {

	public static final PlayerInventoryFrame EMPTY_INVENTORY_FRAME;

	static {
		final ItemStack[] items = new ItemStack[]{
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null};
		final ItemStack[] armors = new ItemStack[]{null, null, null, null};
		EMPTY_INVENTORY_FRAME = new PlayerInventoryFrame(items, armors);
	}

	private final ItemStack[] contents;
	private final ItemStack[] armorContents;

	public PlayerInventoryFrame(ItemStack[] contents, ItemStack[] armorContents) {
		this.contents = contents;
		this.armorContents = armorContents;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public ItemStack[] getArmorContents() {
		return armorContents;
	}

	public static boolean loadInventoryFrame(Player player, PlayerInventoryFrame frame) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		if (frame == null) return false;
		final PlayerInventory inv = player.getInventory();
		inv.setContents(frame.getContents());
		inv.setArmorContents(frame.getArmorContents());
		player.updateInventory();
		return true;
	}
}
