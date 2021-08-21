package mc.obliviate.blokduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class Kit {

	private final PlayerInventory inventory;

	public Kit(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	public static void load(final Kit kit, final Player player) {
		if (kit.getInventory() == null) return;
		player.getInventory().clear();
		PlayerInventory inv = player.getInventory();
		inv.setContents(kit.getInventory().getContents());
		inv.setArmorContents(kit.getInventory().getArmorContents());
	}

	public static void save(final Player player) {
		//save inventory
	}

	public PlayerInventory getInventory() {
		return inventory;
	}
}
