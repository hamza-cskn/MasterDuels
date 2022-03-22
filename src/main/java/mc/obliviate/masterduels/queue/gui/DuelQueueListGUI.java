package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuelQueueListGUI extends GUI implements Listener {

	protected static final List<GUI> OPENED_DUEL_QUEUE_LIST_GUI_LIST = new ArrayList<>();
	public static DuelQueueListGUIConfig guiConfig;

	public DuelQueueListGUI(Player player) {
		super(player, "duel-queue-list-gui", "Queues", 6);
		getPagination().addSlotsBetween(9, 27);
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.add(this);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int i = 9;
		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			addItem(i++, new Icon(guiConfig.getIconOfTemplate(template.getName(), DuelQueue.getAvailableQueues().get(template).getBuilder()))
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

		public DuelQueueListGUIConfig(Map<String, ItemStack> iconItemStacks) {
			this.iconItemStacks = iconItemStacks;
		}

		protected ItemStack getIconOfTemplate(String templateName, GameBuilder builder) {
			ItemStack item = iconItemStacks.get(templateName);
			if (item == null) return XMaterial.BEDROCK.parseItem();

			final int players = builder.getPlayers().size();

			SerializerUtils.applyPlaceholdersOnItemStack(item, new PlaceholderUtil()
					.add("{players}", players + "")
					.add("{max-players}", (builder.getTeamSize() * builder.getTeamAmount()) + "")
					.add("{queue-name}", templateName));

			item.setAmount(Math.max(1, players));

			return item;

		}

	}
}
