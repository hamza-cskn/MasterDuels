package mc.obliviate.masterduels.kit.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.pagination.PaginationManager;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Collection;
import java.util.List;

public class KitSelectionGUI extends ConfigurableGui {

	private static KitSelectionGUI.Config guiConfig;
	private final MatchBuilder builder;
	private final KitSelectResponse response;
	private final List<Kit> allowedKits;
	private final PaginationManager paginationManager = new PaginationManager(this);

	public KitSelectionGUI(Player player, MatchBuilder builder, KitSelectResponse response) {
		this(player, builder, response, null);
	}

	public KitSelectionGUI(Player player, MatchBuilder builder, KitSelectResponse response, List<Kit> allowedKits) {
		super(player, "kit-selection-gui");
		this.builder = builder;
		this.response = response;
		this.allowedKits = allowedKits;

		for (final Kit kit : getAllowedKits()) {
			final Icon icon = new Icon(kit.getIcon().clone()).setName(ChatColor.YELLOW + kit.getKitName()).onClick(e -> {
				player.closeInventory();
				response.onSelected(kit);
			});

			this.paginationManager.addItem(icon);
		}
		this.paginationManager.getSlots().addAll(guiConfig.pageSlots);
	}

	private Collection<Kit> getAllowedKits() {
		if (allowedKits == null || allowedKits.isEmpty()) return Kit.getKits().values();
		return allowedKits;
	}

	@Override
	public void open() {
		if (getAllowedKits().isEmpty()) {
			response.onSelected(null);
			return;
		}
		super.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		putDysfunctionalIcons();
		if (this.paginationManager.getCurrentPage() != this.paginationManager.getLastPage()) {
			putIcon("previous", e -> {
				this.paginationManager.goPreviousPage().update();
			});
		}
		if (this.paginationManager.getCurrentPage() != 0) {
			putIcon("next", e -> {
				this.paginationManager.goNextPage().update();
			});
		}
		this.paginationManager.update();
	}

	@Override
	public String getSectionPath() {
		return "kit-selection-gui";
	}

	public interface KitSelectResponse {

		void onSelected(Kit kit);

	}

	public static class Config {

		private final List<Integer> pageSlots;

		public Config(List<Integer> pageSlots) {
			this.pageSlots = pageSlots;
			KitSelectionGUI.guiConfig = this;
		}
	}

}
