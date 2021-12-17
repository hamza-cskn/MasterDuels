package mc.obliviate.blokduels.gui.kit;

import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class KitSelectionGUI extends GUI {

	private final GameBuilder builder;
	private final KitSelectResponse response;

	public KitSelectionGUI(Player player, GameBuilder builder, KitSelectResponse response) {
		super(player, "kit-selection-gui", "Select a kit", 6);
		this.builder = builder;
		this.response = response;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		int slot = 0;
		for (final Kit kit : Kit.getKits().values()) {
			final Icon icon = new Icon(kit.getIcon().clone()).onClick(e -> {
				player.closeInventory();
				builder.setKit(kit);
				response.onSelected(kit);
			});

			addItem(slot++, icon);
		}
		if (slot == 0) response.onSelected(null);
	}

	public interface KitSelectResponse {

		void onSelected(Kit kit);

	}

}
