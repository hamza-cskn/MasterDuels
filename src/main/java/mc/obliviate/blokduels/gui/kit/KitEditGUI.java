package mc.obliviate.blokduels.gui.kit;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.kit.serializer.KitSerializer;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class KitEditGUI extends GUI {

	private final Kit kit;

	public KitEditGUI(Player player, Kit kit) {
		super(player, "kit-edit-gui", "Kit Editor > " + kit.getKitName(), 6);
		this.kit = kit;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 0);
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 5);

		addAdvancedHytem(4, new Icon(Material.BARRIER)
				.setName(MessageUtils.parseColor("&cChange icon of kit"))
				.setLore("",
						MessageUtils.parseColor("&7Put an Itemstack to set icon"),
						MessageUtils.parseColor("&7of this kit."),
						"",
						MessageUtils.parseColor("&fCurrent: &e" + getMaterialName(kit.getIcon()))
				)).onPut(e -> {
			kit.setIcon(e.getInventory().getItem(4));
		});

		addAdvancedHytem(45, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cChange helmet of kit")).setLore("", MessageUtils.parseColor("&7Put an Itemstack to set helmet"), MessageUtils.parseColor("&7of this kit."), "", MessageUtils.parseColor("&fCurrent: &e" + getMaterialName(kit.getArmorContents()[3]))));
		addAdvancedHytem(46, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cChange chestplate of kit")).setLore("", MessageUtils.parseColor("&7Put an Itemstack to set chestplate"), MessageUtils.parseColor("&7of this kit."), "", MessageUtils.parseColor("&fCurrent: &e" + getMaterialName(kit.getArmorContents()[2]))));
		addAdvancedHytem(47, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cChange leggings of kit")).setLore("", MessageUtils.parseColor("&7Put an Itemstack to set leggings"), MessageUtils.parseColor("&7of this kit."), "", MessageUtils.parseColor("&fCurrent: &e" + getMaterialName(kit.getArmorContents()[1]))));
		addAdvancedHytem(48, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cChange boots of kit")).setLore("", MessageUtils.parseColor("&7Put an Itemstack to set boots"), MessageUtils.parseColor("&7of this kit."), "", MessageUtils.parseColor("&fCurrent: &e" + getMaterialName(kit.getArmorContents()[0]))));

		int slot = 9;
		for (final ItemStack item : kit.getContents()) {
			addItem(slot++, new Icon(item).onClick(e -> {
				e.setCancelled(false);
			}));
		}

		addItem(0, new Icon(Material.ARROW).setName(MessageUtils.parseColor("&cGo Back")).onClick(e -> {
			new KitListGUI(player).open();
		}));

		addItem(8, new Icon(Material.EMERALD_BLOCK).setName(MessageUtils.parseColor("&aSave")).onClick(e -> {
			int index = 0;
			final ItemStack[] items = new ItemStack[36];
			for (final ItemStack item : getInventory().getContents()) {
				if (index > 8 && index < 45) {
					items[index - 9] = item;
				}
				index++;
			}

			final ItemStack[] armors = new ItemStack[4];
			for (int i = 0; i < 4; i++) {
				ItemStack item = getInventory().getItem(48 - i);
				if (item.getType().equals(Material.BARRIER)) {
					item = kit.getArmorContents()[i];
				}
				armors[i] = item;
			}
			final Kit finalKit = new Kit(kit.getKitName(), items, armors, kit.getIcon());
			Kit.getKits().put(kit.getKitName(), finalKit);
			Kit.save((BlokDuels) getPlugin(),finalKit);

			new KitEditGUI(player,finalKit).open();
		}));
	}

	private String getMaterialName(ItemStack item) {
		if (item != null) return item.getType().toString();
		return "AIR";
	}

}
