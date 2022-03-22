package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class DuelQueueTemplate {

	private static final List<DuelQueueTemplate> queueTemplates = new LinkedList<>();
	private final String queueTemplateName;
	private final MasterDuels plugin;
	private final GameDataStorage gameDataStorage;

	public DuelQueueTemplate(final MasterDuels plugin, final String queueTemplateName, ItemStack icon, final Kit kit) {
		this.queueTemplateName = queueTemplateName;
		this.plugin = plugin;
		this.gameDataStorage = gameDataStorage;
		queueTemplates.add(this);
		createNewQueue();
	}

	public static List<DuelQueueTemplate> getQueueTemplates() {
		return queueTemplates;
	}

	public static boolean removeQueueTemplate(String name) {
		final DuelQueueTemplate template = getQueueTemplateFromName(name);
		if (template == null) return false;
		return queueTemplates.remove(template);
	}

	public static DuelQueueTemplate getQueueTemplateFromName(String name) {
		for (DuelQueueTemplate duelQueueTemplate : queueTemplates) {
			if (duelQueueTemplate.getName().equalsIgnoreCase(name)) {
				return duelQueueTemplate;
			}
		}
		return null;
	}

	public void createNewQueue() {
		new DuelQueue(this, new GameBuilder(plugin));
	}

	public String getName() {
		return queueTemplateName;
	}


}
