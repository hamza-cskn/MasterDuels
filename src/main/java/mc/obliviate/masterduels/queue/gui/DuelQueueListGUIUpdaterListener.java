package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.GUI;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.queue.DuelQueueJoinEvent;
import mc.obliviate.masterduels.api.events.queue.DuelQueueLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

	private void updateGuis() {
		if (taskGiven) return;
		taskGiven = true;
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			for (GUI gui : DuelQueueListGUI.OPENED_DUEL_QUEUE_LIST_GUI_LIST) {
				gui.open();
			}
			taskGiven = false;
		}, 5);
	}

	@EventHandler
	public void onQueueLeave(DuelQueueLeaveEvent event) {
		updateGuis();
	}
}