package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class DuelQueueListGUI extends GUI implements Listener {

	public static final List<GUI> OPENED_DUEL_QUEUE_LIST_GUI_LIST = new ArrayList<>(); //todo just make protected this. do not ask anythng.

	public DuelQueueListGUI(Player player) {
		super(player, "duel-queue-list-gui", "Queues", 6);
		getPagination().addSlotsBetween(9, 27);
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.add(this);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int i = 9;
		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			final GameBuilder builder = DuelQueue.getAvailableQueues().get(template).getBuilder();
			addItem(i++, new Icon(template.getIcon())
					.setName(template.getName())
					.setLore("players: " + builder.getPlayers().size() + "/" + (builder.getTeamSize() * builder.getTeamAmount()))
					.setAmount(builder.getPlayers().size())
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
}
