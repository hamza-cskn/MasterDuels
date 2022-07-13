package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.queue.gui.DuelQueueListGUIUpdaterListener;
import org.bukkit.Bukkit;

public class DuelQueueHandler {

	private final MasterDuels plugin;
	public static boolean enabled;

	public DuelQueueHandler(MasterDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		plugin.getConfigurationHandler().initQueues();
		Bukkit.getPluginManager().registerEvents(new DuelQueueListGUIUpdaterListener(plugin), plugin);
	}

}
