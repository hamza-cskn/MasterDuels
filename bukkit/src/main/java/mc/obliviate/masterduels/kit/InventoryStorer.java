package mc.obliviate.masterduels.kit;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryStorer {

	private static final Map<UUID, PlayerInventoryFrame> inventories = new HashMap<>();

	/**
	 * @return true if restore is success.
	 */
	public static boolean restore(final Player player) {
		if (player == null) return false;
		final PlayerInventoryFrame inv = inventories.get(player.getUniqueId());
		if (inv == null) return true;
		player.getInventory().setContents(inv.getContents());
		player.getInventory().setArmorContents(inv.getArmorContents());
		inventories.remove(player.getUniqueId());
		return true;
	}

	public static PlayerInventoryFrame store(final Player player) {
		if (player == null) return null;
		if (inventories.containsKey(player.getUniqueId())) return null;

		final PlayerInventoryFrame playerInventoryFrame = new PlayerInventoryFrame(player.getInventory().getContents(), player.getInventory().getArmorContents());
		inventories.put(player.getUniqueId(), playerInventoryFrame);
		return playerInventoryFrame;
	}


}
