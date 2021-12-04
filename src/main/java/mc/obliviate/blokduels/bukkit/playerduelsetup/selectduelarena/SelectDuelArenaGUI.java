package mc.obliviate.blokduels.bukkit.playerduelsetup.selectduelarena;

import mc.obliviate.blokduels.bukkit.playerduelsetup.DuelSetup;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;

public class SelectDuelArenaGUI extends GUI {

	public SelectDuelArenaGUI(Player player) {
		super(player, "select-duel-arena-gui", "Düello Kur", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int i = 19;
		ArenaSelector.calculate();

		for (ArenaSelector arenaSelector : ArenaSelector.getArenaSelectors().values()) {
			addItem(i, new Icon(Material.BAKED_POTATO).setAmount(arenaSelector.getTeamSize()).setName(arenaSelector.getModeFormat()).onClick(e -> {
				new DuelSetup(player, arenaSelector).open();
			}));

			i = i+3;
		}


	}



}
