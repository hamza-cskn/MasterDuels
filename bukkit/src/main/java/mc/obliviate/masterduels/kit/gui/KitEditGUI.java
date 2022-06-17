package mc.obliviate.masterduels.kit.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.advancedslot.AdvancedSlot;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitEditGUI extends Gui {

	private final Kit kit;
	private ItemStack[] armors;
	private ItemStack displayIcon;

	public KitEditGUI(Player player, Kit kit) {
		super(player, "kit-edit-gui", "Kit Editor > " + kit.getKitName(), 6);
		this.kit = kit;
		armors = kit.getArmorContents().clone();
		displayIcon = kit.getIcon().clone();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 5);

		putKitIcon(50);

		putArmorIcon(45, 46, 47, 48);

		int slot = 9;
		for (final ItemStack item : kit.getContents()) {
			addItem(slot++, new Icon(item).onClick(e -> {
				e.setCancelled(false);
			}));
		}

		addItem(0, new Icon(XMaterial.ARROW.parseItem()).setName(MessageUtils.parseColor("&cGo Back")).onClick(e -> {
			new KitListEditorGUI(player).open();
		}));
	}

	public void putArmorIcon(int helmetSlot, int chestplateSlot, int leggingsSlot, int bootsSlot) {

		//todo search why barriers are invisible in 1.17, 1.18
		final Icon helmetIcon = getScaledItemLoreAndName(armors[3], "&cHelmet slot", "&7Put item to change helmet");
		final Icon chestplateIcon = getScaledItemLoreAndName(armors[2], "&cChestplate slot", "&7Put item to change chestplate");
		final Icon leggingsIcon = getScaledItemLoreAndName(armors[1], "&cLeggings slot", "&7Put item to change leggings");
		final Icon bootsIcon = getScaledItemLoreAndName(armors[0], "&cBoots slot", "&7Put item to change boots");

		addAdvancedIcon(helmetSlot, helmetIcon).onPut(e -> {
			armors[3] = e.getCurrentItem();
		});
		addAdvancedIcon(chestplateSlot, chestplateIcon).onPut(e -> {
			armors[2] = e.getCurrentItem();
		});
		addAdvancedIcon(leggingsSlot, leggingsIcon).onPut(e -> {
			armors[1] = e.getCurrentItem();
		});
		addAdvancedIcon(bootsSlot, bootsIcon).onPut(e -> {
			armors[0] = e.getCurrentItem();
		});
	}

	private void putArmorIcon(Icon icon, int armorPieceIndex, int slot) {

		final AdvancedSlot advancedSlot = addAdvancedIcon(slot, icon).onPut(e -> {
			armors[armorPieceIndex] = e.getCurrentItem();
		});

		if (armors[armorPieceIndex] != null) return;

		getAdvancedSlotManager().putIcon(advancedSlot, armors[armorPieceIndex], null);

	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		int index = 0;
		final ItemStack[] items = new ItemStack[36];
		for (final ItemStack item : getInventory().getContents()) {
			if (index > 8 && index < 45) {
				items[index - 9] = item;
			}
			index++;
		}

		final Kit finalKit = new Kit(kit.getKitName(), items, armors, displayIcon);
		Kit.getKits().put(kit.getKitName(), finalKit);
		Kit.save((MasterDuels) getPlugin(), finalKit);
	}

	public void putKitIcon(int slot) {
		final Icon icon = getScaledItemLoreAndName(kit.getIcon(), "&cDisplay icon of kit", "&7Put item to change icon of kit");

		addAdvancedIcon(slot, icon).onPut(e -> {
			kit.setIcon(e.getCurrentItem());
		});

	}

	public Icon getScaledItemLoreAndName(ItemStack item, String name, String loreLine) {
		item = item.clone();
		final Icon showItem = new Icon(item);


		if (item.getItemMeta() != null) {
			final List<String> lore = showItem.getItem().getItemMeta().getLore();

			if (showItem.getItem().getItemMeta().getDisplayName() != null) {
				showItem.setLore(item.getItemMeta().getDisplayName(), "");
			}
			if (lore != null && !lore.isEmpty()) {
				showItem.appendLore(lore);
			}
		}

		showItem.setName(MessageUtils.parseColor(name));
		showItem.appendLore("", MessageUtils.parseColor(loreLine));

		return showItem;
	}

	private String getMaterialName(ItemStack item) {
		if (item != null) return item.getType().toString();
		return "AIR";
	}

}
