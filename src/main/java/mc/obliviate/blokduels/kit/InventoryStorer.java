package mc.obliviate.blokduels.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryStorer {

	private static final Map<UUID, PlayerInventoryFrame> inventories = new HashMap<>();

	/**
	 * @return restore is success or not.
	 */
	public static boolean restore(final Player player) {
		if (player == null) return false;
		final PlayerInventoryFrame inv = inventories.get(player.getUniqueId());
		if (inv == null) return false;
		player.getInventory().setContents(inv.getContents());
		player.getInventory().setArmorContents(inv.getArmorContents());
		inventories.remove(player.getUniqueId());
		return true;
	}

	public static boolean store(final Player player) {
		if (player == null) return false;
		if (inventories.containsKey(player.getUniqueId())) return false;

		inventories.put(player.getUniqueId(), new PlayerInventoryFrame(player.getInventory().getContents(), player.getInventory().getArmorContents()));
		return true;
	}


}
