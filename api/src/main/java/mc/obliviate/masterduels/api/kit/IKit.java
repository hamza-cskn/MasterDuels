package mc.obliviate.masterduels.api.kit;

import org.bukkit.inventory.ItemStack;

public interface IKit {

	ItemStack[] getArmorContents();

	ItemStack[] getContents();

	String getKitName();

	ItemStack getIcon();

	void setIcon(ItemStack icon);

}
