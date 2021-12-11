package mc.obliviate.blokduels.game.spectator;

import mc.obliviate.blokduels.kit.InventoryStorer;
import mc.obliviate.blokduels.kit.PlayerInventoryFrame;
import org.bukkit.entity.Player;

public class SpectatorManager {

	private static PlayerInventoryFrame spectatorInventoryFrame;

	public static boolean giveSpectatorItems(Player player) {
		if (InventoryStorer.store(player) != null) {
			PlayerInventoryFrame.loadInventoryFrame(player, spectatorInventoryFrame);
			return true;
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
