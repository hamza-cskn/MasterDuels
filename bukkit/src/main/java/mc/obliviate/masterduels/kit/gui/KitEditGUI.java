package mc.obliviate.masterduels.kit.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.advancedslot.AdvancedSlot;
import mc.obliviate.inventory.advancedslot.AdvancedSlotManager;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitEditGUI extends Gui {

	private final Kit kit;
	private ItemStack[] armors;
	private ItemStack displayIcon;
	private final AdvancedSlotManager advancedSlotManager = new AdvancedSlotManager(this);

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

		putArmorIcons(45, 46, 47, 48);

		int slot = 9;
		for (final ItemStack item : kit.getContents()) {
			addItem(slot++, new Icon(item).onClick(e -> {
				e.setCancelled(false);
			}));
		}

		addItem(0, new Icon(XMaterial.ARROW.parseItem()).setName(MessageUtils.parseColor("&cLeave")).setLore(MessageUtils.parseColor("&8Automatically saves")).onClick(e -> {
			new KitListEditorGUI(player).open();
		}));
	}

	public void putArmorIcons(int helmetSlot, int chestplateSlot, int leggingsSlot, int bootsSlot) {
		//todo search why barriers are invisible in 1.17, 1.18
		putArmorIcon(new Icon(Material.BARRIER), 3, helmetSlot);
		putArmorIcon(new Icon(Material.BARRIER), 2, chestplateSlot);
		putArmorIcon(new Icon(Material.BARRIER), 1, leggingsSlot);
		putArmorIcon(new Icon(Material.BARRIER), 0, bootsSlot);
	}

	private void putArmorIcon(Icon icon, int armorPieceIndex, int slot) {
		final AdvancedSlot advancedSlot = this.advancedSlotManager.addAdvancedIcon(slot, icon).onPreClick((e, item) -> {
			if (e != null) {
				this.armors[armorPieceIndex] = item;
			}
			return false;
		}).onPickup(e -> {
			if (e != null && e.getCurrentItem() == null) {
				return;
			}
			this.armors[armorPieceIndex] = null;
		});

		if (armors[armorPieceIndex] == null) return;
		this.advancedSlotManager.putIconToAdvancedSlot(advancedSlot, armors[armorPieceIndex], null);
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

		addItem(45, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cHelmet")));
		addItem(46, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cChestplate")));
		addItem(47, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cLeggings")));
		addItem(48, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("&cBoots")));

		final Kit finalKit = new Kit(kit.getKitName(), items, armors, displayIcon);
		Kit.getKits().put(kit.getKitName(), finalKit);
		Kit.save((MasterDuels) getPlugin(), finalKit);
	}

	public void putKitIcon(int slot) {
		final Icon icon = getScaledItemLoreAndName(kit.getIcon(), "&cDisplay icon of kit", "&7Put item to change icon of kit");

		AdvancedSlot advancedSlot = this.advancedSlotManager.addAdvancedIcon(slot, icon).onPut(e -> {
			if (e != null)
				displayIcon = e.getCurrentItem();
		});
		this.advancedSlotManager.putIconToAdvancedSlot(advancedSlot, displayIcon, null);

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
}
