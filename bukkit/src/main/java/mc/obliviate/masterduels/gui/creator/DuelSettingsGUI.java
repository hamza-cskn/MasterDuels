package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.gui.GUISerializerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DuelSettingsGUI extends ConfigurableGui {

	private static Config guiConfig;
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

		for (final GameRule rule : GameRule.values()) {
			if (!rule.doesSupport()) continue;
			RuleIcon ruleIcon = guiConfig.iconMap.get(rule);
			if (ruleIcon == null) continue;

			Icon icon;
			if (matchCreator.getBuilder().getRules().contains(rule)) {
				icon = new Icon(ruleIcon.enabledItem.clone()).onClick(e -> {
					matchCreator.getBuilder().removeRule(rule);
					open();
				});
			} else {
				icon = new Icon(ruleIcon.disabledItem.clone()).onClick(e -> {
					matchCreator.getBuilder().addRule(rule);
					open();
				});
			}

			addItem(ruleIcon.slot, icon);

		}

		putIcon("back", e -> {
			new DuelMatchCreatorGUI(player, matchCreator).open();
		});

	}

	@Override
	public String getSectionPath() {
		return "duel-creator.game-rules-gui";
	}

	public static class Config {

		private final Map<GameRule, RuleIcon> iconMap = new HashMap<>();

		public Config(ConfigurationSection section) {
			DuelSettingsGUI.guiConfig = this;
			loadIcons(section.getConfigurationSection("icons"));
		}

		private void loadIcons(ConfigurationSection section) {
			for (GameRule rule : GameRule.values()) {
				final int slot = section.getInt(rule.name() + ".slot");
				final ItemStack on = GUISerializerUtils.getConfigItem(section.getConfigurationSection(rule.name() + ".enabled"));
				final ItemStack off = GUISerializerUtils.getConfigItem(section.getConfigurationSection(rule.name() + ".disabled"));

				iconMap.put(rule, new RuleIcon(slot, on, off));
			}
		}
	}

	private static class RuleIcon {

		private final int slot;
		private final ItemStack enabledItem;
		private final ItemStack disabledItem;

		private RuleIcon(int slot, ItemStack enabledItem, ItemStack disabledItem) {
			this.slot = slot;
			this.enabledItem = enabledItem;
			this.disabledItem = disabledItem;
		}

	}

}
