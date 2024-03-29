package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.queue.DuelQueueJoinEvent;
import mc.obliviate.masterduels.api.queue.DuelQueueLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class DuelQueueListGUIUpdaterListener implements Listener {

    private final MasterDuels plugin;
    private boolean taskGiven = false;

    public DuelQueueListGUIUpdaterListener(MasterDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQueueJoin(DuelQueueJoinEvent event) {
        updateGuis();
    }

    @EventHandler
    public void onQueueLeave(DuelQueueLeaveEvent event) {
        updateGuis();
    }

    private void updateGuis() {
        if (taskGiven) return;
        taskGiven = true;
        for (Gui gui : new ArrayList<>(DuelQueueListGUI.OPENED_DUEL_QUEUE_LIST_GUI_LIST)) {
            gui.open();
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            taskGiven = false;
        }, 5);
    }
}
