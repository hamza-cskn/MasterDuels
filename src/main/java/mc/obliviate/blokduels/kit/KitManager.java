package mc.obliviate.blokduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager {

	private final Map<UUID, PlayerInventory> inventories = new HashMap<>();

	public Map<UUID, PlayerInventory> getInventories() {
		return inventories;
	}

	/**
	 * @return restore is success or not.
	 */
	public boolean restore(final Player player) {
		if (player == null) return false;
		final PlayerInventory inv = inventories.get(player.getUniqueId());
		if (inv == null) return false;
		player.getInventory().setContents(inv.getContents());
		player.getInventory().setArmorContents(inv.getArmorContents());
		return true;
	}
}
