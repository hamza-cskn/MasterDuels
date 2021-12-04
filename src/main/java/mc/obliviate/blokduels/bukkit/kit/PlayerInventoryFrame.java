package mc.obliviate.blokduels.bukkit.kit;

import org.bukkit.inventory.ItemStack;

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
}
