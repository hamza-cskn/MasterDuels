package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DuelSettingsGUI extends ConfigurableGui {

	private final MatchCreator matchCreator;
	private final boolean isOwner;

	public DuelSettingsGUI(final Player player, final MatchCreator matchCreator) {
		super(player, "duel-rules-gui");
		this.matchCreator = matchCreator;
		this.isOwner = matchCreator.getOwnerPlayer().equals(player.getUniqueId());
	}

	@Override
	public void onOpen(final InventoryOpenEvent event) {
		putDysfunctionalIcons();

		putIcon("back", e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		});

		int i = 9;
		for (final GameRule rule : MatchCreator.ALLOWED_GAME_RULES) {
			Icon icon;
			if (matchCreator.getBuilder().getRules().contains(rule)) {
				icon = new Icon(XMaterial.LIME_DYE.parseItem()).setName(MessageUtils.parseColor("&a" + rule.name()));
				if (isOwner) {
					icon.onClick(e -> {
						matchCreator.getBuilder().removeRule(rule);
						open();
					});
				}
			} else {
				icon = new Icon(XMaterial.GRAY_DYE.parseItem()).setName(MessageUtils.parseColor("&c" + rule.name()));
				if (isOwner) {
					icon.onClick(e -> {
						matchCreator.getBuilder().addRule(rule);
						open();
					});
				}
			}
			addItem(i++, icon);
		}
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.game-rules-gui";
	}

}
