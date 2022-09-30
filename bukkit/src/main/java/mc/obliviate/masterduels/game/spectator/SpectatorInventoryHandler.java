package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.kit.PlayerInventoryFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static mc.obliviate.masterduels.kit.PlayerInventoryFrame.EMPTY_INVENTORY_FRAME;

public class SpectatorInventoryHandler {

	public static void giveSpectatorItems(Player player) {
		if (InventoryStorer.store(player) != null) {
			PlayerInventoryFrame.loadInventoryFrame(player, EMPTY_INVENTORY_FRAME);
		}

		final int settingsToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.settings-tool.slot");
		ItemStack settingsTool = ItemStackSerializer.deserializeItemStack(ConfigurationHandler.getConfig().getConfigurationSection("spectator.spectator-items.settings-tool"));

		final int teleportationToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.teleportation-tool.slot");
		ItemStack teleportationTool = ItemStackSerializer.deserializeItemStack(ConfigurationHandler.getConfig().getConfigurationSection("spectator.spectator-items.teleportation-tool"));

		final int leaveToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.leave-tool.slot");
		ItemStack leaveTool = ItemStackSerializer.deserializeItemStack(ConfigurationHandler.getConfig().getConfigurationSection("spectator.spectator-items.leave-tool"));

		player.getInventory().setItem(leaveToolSlot, leaveTool);
		player.getInventory().setItem(teleportationToolSlot, teleportationTool);
		player.getInventory().setItem(settingsToolSlot, settingsTool);
	}
}
