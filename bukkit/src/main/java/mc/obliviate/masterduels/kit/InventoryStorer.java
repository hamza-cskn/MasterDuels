package mc.obliviate.masterduels.kit;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryStorer {

	private static final Map<UUID, PlayerInventoryFrame> INVENTORY_FRAME_MAP = new HashMap<>();

	/**
	 * @return true if restore is success.
	 */
	public static boolean restore(final Player player) {
		if (player == null) return false;
		final PlayerInventoryFrame inv = INVENTORY_FRAME_MAP.get(player.getUniqueId());
		if (inv == null) return true;
		player.getInventory().setContents(inv.getContents());
		player.getInventory().setArmorContents(inv.getArmorContents());
		INVENTORY_FRAME_MAP.remove(player.getUniqueId());
		return true;
	}

	public static PlayerInventoryFrame store(final Player player) {
		if (player == null) return null;
		if (INVENTORY_FRAME_MAP.containsKey(player.getUniqueId())) return null;

		final PlayerInventoryFrame playerInventoryFrame = new PlayerInventoryFrame(player.getInventory().getContents(), player.getInventory().getArmorContents());
		INVENTORY_FRAME_MAP.put(player.getUniqueId(), playerInventoryFrame);
		return playerInventoryFrame;
	}


}
