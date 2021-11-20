package mc.obliviate.blokduels.gui;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.arena.elements.Positions;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.setup.PositionSelection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;

import java.util.Map;

public class DuelArenaListGUI extends GUI {

	public DuelArenaListGUI(Player player) {
		super(player, "duel-games-list-gui", "Current Duel Games", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;
		for (final Arena arena : DataHandler.getArenas().keySet()) {
			final Icon hytem = new Icon(Material.CAULDRON_ITEM)
					.setName("§9" + arena.getName())
					.setLore("§7Map: §d" + arena.getMapName(),
							"§7Mode: §d" + convertMode(arena.getTeamSize(), arena.getTeamAmount()),
							"§5§lDEBUG",
							"§7Pos1: "+  PositionSelection.formatLocation(arena.getArenaCuboid().getPoint1()),
							"§7Pos2: "+  PositionSelection.formatLocation(arena.getArenaCuboid().getPoint2()),
							"");

			for (Map.Entry<String, Positions> positionsEntry : arena.getPositions().entrySet()) {
				hytem.appendLore("§6" + positionsEntry.getKey());
				for (Map.Entry<Integer, Location> locationsEntry : positionsEntry.getValue().getLocations().entrySet()) {
					hytem.appendLore(" §e" + locationsEntry.getKey() + " §a= " + PositionSelection.formatLocation(locationsEntry.getValue()));
				}
			}
			addItem(slot++, hytem);
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
