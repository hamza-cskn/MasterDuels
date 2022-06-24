package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.gui.GUISerializerUtils;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuelQueueListGUI extends Gui implements Listener {

	protected static final List<Gui> OPENED_DUEL_QUEUE_LIST_GUI_LIST = new ArrayList<>();
	public static DuelQueueListGUIConfig guiConfig;

	public DuelQueueListGUI(Player player) {
		super(player, "duel-queue-list-gui", guiConfig.title, guiConfig.size);

		getPaginationManager().registerSlotsBetween(9, 27);

		calculateIcons();
		getPaginationManager().firstPage();

	}


	@Override
	public void onOpen(InventoryOpenEvent event) {
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.add(this);

		GUISerializerUtils.putDysfunctionalIcons(this, guiConfig.iconsSection.getConfigurationSection("dysfunctional-icons"));

		if (DuelQueueTemplate.getQueueTemplates().isEmpty()) {
			addItem(GUISerializerUtils.getConfigSlot(guiConfig.iconsSection.getConfigurationSection("functional-icons.other.no-queue-found")),
					GUISerializerUtils.getConfigItem(guiConfig.iconsSection.getConfigurationSection("functional-icons.other.no-queue-found")));
		} else {
			getPaginationManager().update();
		}

		if (getPaginationManager().getLastPage() != getPaginationManager().getPage()) {
			addItem(8, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
				getPaginationManager().nextPage();
				open();
			}));
		}
		if (getPaginationManager().getPage() != 0) {
			addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
				getPaginationManager().previousPage();
				open();
			}));
		}

		player.updateInventory();
	}

	private void calculateIcons() {
		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			getPaginationManager().addIcon(new Icon(guiConfig.getIconOfTemplate(template.getName(), DuelQueue.getAvailableQueues().get(template).getBuilder()))
					.onClick(e -> {
						player.performCommand("duel queue join " + template.getName());
						player.closeInventory();
					}));
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.remove(this);
	}

	public static class DuelQueueListGUIConfig {

		public final Map<String, ItemStack> iconItemStacks;
		private final int zeroAmount;
		private final List<Integer> slots = new ArrayList<>();
		private final int size;
		private final String title;
		private final ConfigurationSection iconsSection;

		public DuelQueueListGUIConfig(int zeroAmount, int size, String title, Map<String, ItemStack> iconItemStacks, ConfigurationSection iconsSection) {
			this.zeroAmount = zeroAmount;
			this.size = size;
			this.title = title;
			this.iconItemStacks = iconItemStacks;
			this.iconsSection = iconsSection;
		}

		protected ItemStack getIconOfTemplate(String templateName, MatchBuilder builder) {
			ItemStack item = iconItemStacks.get(templateName).clone();
			if (item == null) return XMaterial.BEDROCK.parseItem();

			final int players = builder.getPlayers().size();

			SerializerUtils.applyPlaceholdersOnItemStack(item, new PlaceholderUtil()
					.add("{players}", players + "")
					.add("{max-players}", (builder.getTeamSize() * builder.getTeamAmount()) + "")
					.add("{queue-name}", templateName));

			item.setAmount(Math.max(zeroAmount, players));

			return item;

		}

	}
}
