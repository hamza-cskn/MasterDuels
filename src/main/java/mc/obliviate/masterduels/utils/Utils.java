package mc.obliviate.masterduels.utils;

import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Utils {

	public static final List<ItemStack> teamIcons = Arrays.asList(
			XMaterial.RED_STAINED_GLASS.parseItem(),
			XMaterial.BLUE_STAINED_GLASS.parseItem(),
			XMaterial.YELLOW_STAINED_GLASS.parseItem(),
			XMaterial.LIME_STAINED_GLASS.parseItem(),
			XMaterial.PURPLE_STAINED_GLASS.parseItem(),
			XMaterial.CYAN_STAINED_GLASS.parseItem(),
			XMaterial.ORANGE_STAINED_GLASS.parseItem(),
			XMaterial.PINK_STAINED_GLASS.parseItem(),
			XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem(),
			XMaterial.GREEN_STAINED_GLASS.parseItem());

	public static int getPercentage(final double total, final double progress) {
		try {
			return (int) (progress / (total / 100d));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
