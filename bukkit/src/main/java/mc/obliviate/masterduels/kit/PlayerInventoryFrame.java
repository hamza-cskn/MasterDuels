package mc.obliviate.masterduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryFrame {

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
