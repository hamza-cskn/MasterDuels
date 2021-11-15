package mc.obliviate.blokduels.playerduelsetup;

import mc.obliviate.blokduels.playerduelsetup.selectduelarena.ArenaSelector;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;

public class DuelSetup extends GUI {

	private final ArenaSelector arenaSelector;

	public DuelSetup(Player player, ArenaSelector arenaSelector) {
		super(player, "duel-setup-gui", "Düello " + arenaSelector.getModeFormat(), 6);
		this.arenaSelector = arenaSelector;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		for (int amount = 1; amount <= arenaSelector.getTeamAmount(); amount++) {
			for (int size = 1; size <= arenaSelector.getTeamSize(); size++) {

				final int slot = size + ((amount-1)*9+9);
				addItem(slot, new Icon(Material.STAINED_GLASS_PANE).setName(amount + ". team").appendLore(size + ". player"));
			}
		}


		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15),5);
		addItem(49, new Icon(Material.EMERALD_BLOCK));

	}


}
