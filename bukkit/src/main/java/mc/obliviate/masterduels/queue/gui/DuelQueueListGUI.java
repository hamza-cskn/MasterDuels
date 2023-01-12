package mc.obliviate.masterduels.queue.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuelQueueListGUI extends ConfigurableGui {

	protected static final List<Gui> OPENED_DUEL_QUEUE_LIST_GUI_LIST = new ArrayList<>();
	public static Config guiConfig;

	public DuelQueueListGUI(Player player) {
		super(player, "duel-queue-list-gui");
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.add(this);
		putDysfunctionalIcons();

		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			final int slot = ConfigurationHandler.getMenusSection("queues-gui.icons.queue-icons." + template.getName()).getInt("slot");
			addItem(slot, new Icon(guiConfig.getIconOfTemplate(template, DuelQueue.getAvailableQueues().get(template).getBuilder()))
                    .onClick(e -> {
                        player.closeInventory();
                        player.performCommand("duel queue join " + template.getName());
                    }));
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		OPENED_DUEL_QUEUE_LIST_GUI_LIST.remove(this);
	}

	@Override
	public String getSectionPath() {
		return "queues-gui";
	}

	public static class Config {
		public final Map<String, ItemStack> iconItemStacks;
		private final int zeroAmount;
		private final List<Integer> slots = new ArrayList<>();
		private final int size;
		private final String title;
		private final ConfigurationSection iconsSection;

        public Config(int zeroAmount, int size, String title, Map<String, ItemStack> iconItemStacks, ConfigurationSection iconsSection) {
            this.zeroAmount = zeroAmount;
            this.size = size;
            this.title = title;
            this.iconItemStacks = iconItemStacks;
            this.iconsSection = iconsSection;
            DuelQueueListGUI.guiConfig = this;
        }

        protected ItemStack getIconOfTemplate(DuelQueueTemplate template, MatchBuilder builder) {
            String templateName = template.getName();
            ItemStack item = iconItemStacks.get(templateName).clone();
            if (item == null) return XMaterial.BEDROCK.parseItem();

            final int players = builder.getPlayers().size();

            ItemStackSerializer.applyPlaceholdersToItemStack(item, new PlaceholderUtil()
                    .add("{players}", players + "")
                    .add("{total-players}", template.getQueues().stream().filter(queue -> queue.getMatch() != null).mapToInt(queue -> queue.getMatch().getPlayers().size()).sum() + "")
                    .add("{max-players}", (builder.getTeamSize() * builder.getTeamAmount()) + "")
                    .add("{queue-name}", templateName));

			item.setAmount(Math.max(zeroAmount, players));

			return item;

		}

	}
}
