package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class DuelQueueTemplate {

	private static final List<DuelQueueTemplate> queueTemplates = new LinkedList<>();
	private final String queueTemplateName;
	private ItemStack icon;
	private final Kit kit;

	public DuelQueueTemplate(final String queueTemplateName, ItemStack icon, final Kit kit) {
		this.queueTemplateName = queueTemplateName;
		this.icon = icon;
		this.kit = kit;
		queueTemplates.add(this);
	}

	public static List<DuelQueueTemplate> getQueueTemplates() {
		return queueTemplates;
	}

	public static void removeQueue(String name) {
		final DuelQueueTemplate template = getQueueFromName(name);
	}

	public Kit getKit() {
		return kit;
	}

	public String getName() {
		return queueTemplateName;
	}

	public ItemStack getIcon() {
		if (icon == null) return kit.getIcon();
		return icon;
	}

	public static DuelQueueTemplate getQueueFromName(String name) {
		for (DuelQueueTemplate duelQueueTemplate : queueTemplates) {
			if (duelQueueTemplate.getName().equalsIgnoreCase(name)) {
				return duelQueueTemplate;
			}
		}
		return null;
	}
}
