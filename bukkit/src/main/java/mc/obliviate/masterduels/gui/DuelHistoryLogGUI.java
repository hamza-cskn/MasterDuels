package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.history.MatchHistoryLog;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DuelHistoryLogGUI extends ConfigurableGui {

	public static DuelHistoryLogGUIConfig guiConfig;

	public DuelHistoryLogGUI(Player player) {
		super(player, "duel-history-log-gui");
		getPaginationManager().registerItemSlots(9, 53);
		for (final MatchHistoryLog log : MatchHistoryLog.historyCache) {
			HistoryIconType type = HistoryIconType.SOLO;
			if (log.getLosers().size() > 1) {
				type = HistoryIconType.NON_SOLO;
			}
			getPaginationManager().addIcon(guiConfig.deserializeIcon(type, log));
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		putDysfunctionalIcons();
	}

	@Override
	public String getSectionPath() {
		return "game-history-gui";
	}

	private enum HistoryIconType {
		SOLO,
		NON_SOLO
	}

	public static class DuelHistoryLogGUIConfig {

		private final Map<HistoryIconType, ItemStack> historyIconItemStacks = new HashMap<>();
		private final String winnersFormat;
		private final String losersFormat;

		public DuelHistoryLogGUIConfig(ConfigurationSection guiSection) {
			historyIconItemStacks.put(HistoryIconType.SOLO, GUISerializerUtils.getConfigItem(guiSection.getConfigurationSection("solo-games-icon")));
			historyIconItemStacks.put(HistoryIconType.NON_SOLO, GUISerializerUtils.getConfigItem(guiSection.getConfigurationSection("non-solo-games-icon")));
			winnersFormat = guiSection.getString("winners-format");
			losersFormat = guiSection.getString("losers-format");
		}

		private Icon deserializeIcon(final HistoryIconType type, MatchHistoryLog log) {
			final Icon icon = new Icon(historyIconItemStacks.get(type).clone());
			List<String> description = icon.getItem().getItemMeta().getLore(); //raw-placeholder lore
			if (description == null) description = new ArrayList<>();
			icon.setLore(new ArrayList<>());

			final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{time}", TimerUtils.formatTimeDifferenceAsTime(log.getStartTime(), log.getEndTime()))
					.add("{played-date}", TimerUtils.formatDate(log.getStartTime()));
			if (log.getWinners().size() == 1) {
				placeholderUtil.add("{winner}", Bukkit.getOfflinePlayer(log.getWinners().get(0)).getName());
			}
			if (log.getLosers().size() == 1) {
				placeholderUtil.add("{loser}", Bukkit.getOfflinePlayer(log.getLosers().get(0)).getName());
			}
			for (final String line : description) {
				if (line.equalsIgnoreCase("{+winners}")) {
					for (final UUID uuid : log.getWinners()) {
						icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(winnersFormat, new PlaceholderUtil().add("{winner}", Bukkit.getOfflinePlayer(uuid).getName()))));
					}
					continue;
				} else if (line.equalsIgnoreCase("{+losers}")) {
					for (final UUID uuid : log.getLosers()) {
						icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(losersFormat, new PlaceholderUtil().add("{loser}", Bukkit.getOfflinePlayer(uuid).getName()))));
					}
					continue;
				}
				icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(line, placeholderUtil)));
			}

			icon.setName(MessageUtils.parseColor(MessageUtils.applyPlaceholders(icon.getItem().getItemMeta().getDisplayName(), placeholderUtil)));

			return icon;
		}
	}
}
