package mc.obliviate.masterduels.gui.room;

import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
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
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()),0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameBuilder).open();
		}));

		int i = 9;
		for (final GameRule rule : GameRule.values()) {

			Icon icon;
			if (gameBuilder.getGameRules().contains(rule)) {
				icon = new Icon(XMaterial.LIME_DYE.parseItem()).setName(MessageUtils.parseColor("&a" + rule.name()));
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
