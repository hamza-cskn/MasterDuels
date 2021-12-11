package mc.obliviate.blokduels.game.spectator;

import mc.obliviate.blokduels.kit.InventoryStorer;
import mc.obliviate.blokduels.kit.PlayerInventoryFrame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectatorManager {

	private static PlayerInventoryFrame spectatorInventoryFrame;

	static {
		final ItemStack[] items = new ItemStack[]{
				null, null, null, null, null, null, null, null, new ItemStack(Material.BARRIER),
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null};

		final ItemStack[] armors = new ItemStack[]{null, null, null, null};
		spectatorInventoryFrame = new PlayerInventoryFrame(items, armors);
	}

	public static boolean giveSpectatorItems(Player player) {
		if (InventoryStorer.store(player) != null) {
			return PlayerInventoryFrame.loadInventoryFrame(player, spectatorInventoryFrame);
		}
		return false;
	}

	public static PlayerInventoryFrame getSpectatorInventoryFrame() {
		return spectatorInventoryFrame;
	}

	public static void setSpectatorInventoryFrame(PlayerInventoryFrame spectatorInventoryFrame) {
		SpectatorManager.spectatorInventoryFrame = spectatorInventoryFrame;
	}
}
