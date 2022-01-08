package mc.obliviate.masterduels.gui.room;

import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelTeamManagerGUI extends GUI {

	final GameBuilder gameBuilder;

	public DuelTeamManagerGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-settings-gui", "Settings", gameBuilder.getTeamAmount() + 1);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));

		for (int team = 0; team < gameBuilder.getTeamAmount(); team++) {
			addItem((team + 1) * 9, Utils.teamIcons.get(team));
			for (int member = 0; member < gameBuilder.getTeamSize(); member++) {
				final int slot = ((team + 1) * 9 + 1 + member);
				if (slot >= getSize()) {
					player.sendMessage("§cThere are toooo many member!");
					return;
				}



			}
		}
	}
}
