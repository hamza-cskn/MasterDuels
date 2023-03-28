package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.gui.spectator.SpectatorSettingsGUI;
import mc.obliviate.masterduels.gui.spectator.SpectatorTeleportationGUI;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpectatorListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final IUser iUser = UserHandler.getUser(event.getPlayer().getUniqueId());
        if (!(iUser instanceof Spectator)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        final int settingsToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.settings-tool.slot");
        final int teleportationToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.teleportation-tool.slot");
        final int leaveToolSlot = ConfigurationHandler.getConfig().getInt("spectator.spectator-items.leave-tool.slot");

        if (event.getPlayer().getInventory().getHeldItemSlot() == settingsToolSlot) {
            event.setCancelled(true);
            new SpectatorSettingsGUI(event.getPlayer()).open();
        } else if (event.getPlayer().getInventory().getHeldItemSlot() == teleportationToolSlot) {
            event.setCancelled(true);
            new SpectatorTeleportationGUI(event.getPlayer(), ((Spectator) iUser).getMatch()).open();
        } else if (event.getPlayer().getInventory().getHeldItemSlot() == leaveToolSlot) {
            event.setCancelled(true);
            event.getPlayer().performCommand("duel leave");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final IUser iUser = UserHandler.getUser(event.getWhoClicked().getUniqueId());
        if (!(iUser instanceof Spectator)) return;

        event.setCancelled(true);
    }
}
