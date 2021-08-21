package mc.obliviate.blokduels.gui;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

public class DuelGamesListGUI extends GUI {

	public DuelGamesListGUI(Player player) {
		super(player, "duel-games-list-gui", "Current Duel Games", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;
		for (final Arena arena : DataHandler.getArenas().keySet()) {
			addItem(slot++, new Hytem(Material.CAULDRON_ITEM)
					.setName("§9" + arena.getName())
					.setLore("§7Map: §d" + arena.getMapName(),
							"§7Mode: §d" + convertMode(arena.getTeamSize(), arena.getTeamAmount())));
		}
	}

	private static String convertMode(int size, int amount) {
		final StringBuilder sb = new StringBuilder();
		for (;amount > 0; amount--) {
			sb.append(size);
			if (amount != 1) {
				sb.append("v");
			}
		}

		return sb.toString();
	}
}
