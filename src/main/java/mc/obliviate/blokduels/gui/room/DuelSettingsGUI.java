package mc.obliviate.blokduels.gui.room;

import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.game.gamerule.GameRule;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelSettingsGUI extends GUI {

	final GameBuilder gameBuilder;

	public DuelSettingsGUI(Player player, GameBuilder gameBuilder) {
		super(player, "duel-settings-gui", "Settings", 4);
		this.gameBuilder = gameBuilder;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));

		int i = 9;
		for (final GameRule rule : GameRule.values()) {

			Icon icon;
			if (gameBuilder.getGameRules().contains(rule)) {
				icon = new Icon(XMaterial.GREEN_DYE.parseItem()).setName(MessageUtils.parseColor("&a" + rule.name()));
				icon.onClick(e -> {
					gameBuilder.removeGameRule(rule);
					open();
				});
			} else {
				icon = new Icon(XMaterial.GRAY_DYE.parseItem()).setName(MessageUtils.parseColor("&c" + rule.name()));
				icon.onClick(e -> {
					gameBuilder.addGameRule(rule);
					open();
				});
			}
			addItem(i++, icon);



		}
	}


}
