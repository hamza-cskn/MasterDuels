package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.utils.StringUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelSettingsGUI extends GUI {

	private final GameBuilder gameBuilder;
	private final GameCreator gameCreator;

	public DuelSettingsGUI(final Player player, final GameCreator gameCreator) {
		super(player, "duel-settings-gui", "Settings", 4);
		this.gameBuilder = gameCreator.getBuilder();
		this.gameCreator = gameCreator;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelGameCreatorGUI(player, gameCreator).open();
		}));

		int i = 9;
		for (final GameRule rule : GameCreator.ALLOWED_GAME_RULES) {

			Icon icon;
			if (gameBuilder.getGameRules().contains(rule)) {
				icon = new Icon(XMaterial.LIME_DYE.parseItem()).setName(StringUtils.parseColor("&a" + rule.name()));
				icon.onClick(e -> {
					gameBuilder.removeGameRule(rule);
					open();
				});
			} else {
				icon = new Icon(XMaterial.GRAY_DYE.parseItem()).setName(StringUtils.parseColor("&c" + rule.name()));
				icon.onClick(e -> {
					gameBuilder.addGameRule(rule);
					open();
				});
			}
			addItem(i++, icon);
		}
	}


}
