package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelSettingsGUI extends Gui {

	private final MatchBuilder matchBuilder;
	private final MatchCreator matchCreator;

	public DuelSettingsGUI(final Player player, final MatchCreator matchCreator) {
		super(player, "duel-settings-gui", "Settings", 4);
		this.matchBuilder = matchCreator.getBuilder();
		this.matchCreator = matchCreator;
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);
		addItem(0, new Icon(XMaterial.ARROW.parseItem()).onClick(e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		}));

		int i = 9;
		for (final GameRule rule : MatchCreator.ALLOWED_GAME_RULES) {

			Icon icon;
			if (matchBuilder.getGameRules().contains(rule)) {
				icon = new Icon(XMaterial.LIME_DYE.parseItem()).setName(MessageUtils.parseColor("&a" + rule.name()));
				icon.onClick(e -> {
					matchBuilder.removeGameRule(rule);
					open();
				});
			} else {
				icon = new Icon(XMaterial.GRAY_DYE.parseItem()).setName(MessageUtils.parseColor("&c" + rule.name()));
				icon.onClick(e -> {
					matchBuilder.addGameRule(rule);
					open();
				});
			}
			addItem(i++, icon);
		}
	}


}
