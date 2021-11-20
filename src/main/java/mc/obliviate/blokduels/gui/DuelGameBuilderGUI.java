package mc.obliviate.blokduels.gui;

import mc.obliviate.inventory.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelGameBuilderGUI extends GUI {

	public DuelGameBuilderGUI(Player player) {
		super(player, "duel-game-builder-gui", "Duel Game Builder", 6);
	}


	@Override
	public void onOpen(InventoryOpenEvent event) {

	}
}
