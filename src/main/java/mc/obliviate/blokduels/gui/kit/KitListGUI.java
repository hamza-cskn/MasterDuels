package mc.obliviate.blokduels.gui.kit;

import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.List;

public class KitListGUI extends GUI {

	public KitListGUI(Player player) {
		super(player, "kit-list-gui", "Kit Editor", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 0);

		int slot = 9;
		for (final Kit kit : Kit.getKits().values()) {

			final Icon icon = new Icon(kit.getIcon().clone());

			final List<String> lore = new ArrayList<>();
			if (icon.getItem().getItemMeta() != null) {
				if (icon.getItem().getItemMeta().getLore() != null) {
					lore.addAll(icon.getItem().getItemMeta().getLore());
				}
				icon.setLore(icon.getItem().getItemMeta().getDisplayName(), "");
			}
			icon.appendLore(lore);
			icon.appendLore("", MessageUtils.parseColor("&eClick to edit this kit!"));
			icon.setName(MessageUtils.parseColor("&d&l" + kit.getKitName()));

			icon.onClick(e -> {
				new KitEditGUI(player, kit).open();
			});

			addItem(slot++, icon);

		}
	}
}
