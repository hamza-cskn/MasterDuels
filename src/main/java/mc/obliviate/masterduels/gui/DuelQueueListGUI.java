package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelQueueListGUI extends GUI {

	public DuelQueueListGUI(Player player) {
		super(player, "duel-queue-list-gui", "Queues", 6);

		getPagination().addSlotsBetween(9, 27);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int i = 9;
		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			addItem(i++, new Icon(template.getIcon()).onClick(e -> {
				player.performCommand("duel queue join " + template.getName());
				player.closeInventory();
			}));
		}

	}
}
