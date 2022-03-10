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
	private final Kit kit;
	private ItemStack icon;
	private final MasterDuels plugin;

	public DuelQueueTemplate(final MasterDuels plugin, final String queueTemplateName, ItemStack icon, final Kit kit) {
		this.queueTemplateName = queueTemplateName;
		this.icon = icon;
		this.kit = kit;
		this.plugin = plugin;
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

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}
}
