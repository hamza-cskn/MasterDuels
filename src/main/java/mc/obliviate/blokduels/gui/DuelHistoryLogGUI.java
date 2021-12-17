package mc.obliviate.blokduels.gui;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.history.GameHistoryLog;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DuelHistoryLogGUI extends GUI {

	public static ConfigurationSection guiSection;

	public DuelHistoryLogGUI(Player player) {
		super(player, "duel-history-log-gui", "Title could not loaded", 6);
		setTitle(guiSection.getString("title", "Title could not loaded"));
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 0);

		int slot = 9;
		for (final GameHistoryLog log : GameHistoryLog.historyCache) {

			ConfigurationSection section;
			if (log.getLosers().size() == 1) {
				section = guiSection.getConfigurationSection("solo-games-icon");
			} else {
				section = guiSection.getConfigurationSection("non-solo-games-icon");
			}

			addItem(slot++, deserializeIcon(section, log));
		}
	}

	private Icon deserializeIcon(final ConfigurationSection section, GameHistoryLog log) {
		if (section == null) return new Icon(Material.BEDROCK).setName("Item could not deserialized.");

		final Material material = Material.getMaterial(section.getString("material-type"));
		final Icon icon = new Icon(material);
		final List<String> description = section.getStringList("description");

		final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{time}", TimerUtils.getFormattedDifferentTime(log.getStartTime(), log.getEndTime()))
				.add("{played-date}", new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date(log.getStartTime())));
		if (log.getLosers().size() == 1) {
			placeholderUtil.add("{winner}", Bukkit.getOfflinePlayer(log.getWinners().get(0)).getName());
			placeholderUtil.add("{loser}", Bukkit.getOfflinePlayer(log.getLosers().get(0)).getName());
		}

		for (final String line : description.subList(1,description.size())) {
			if (line.equalsIgnoreCase("{+winners}")) {
				for (final UUID uuid : log.getWinners()) {
					icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(section.getString("winners-format"), new PlaceholderUtil().add("{winner}", Bukkit.getOfflinePlayer(uuid).getName()))));
				}
				continue;
			} else if (line.equalsIgnoreCase("{+losers}")) {
				for (final UUID uuid : log.getLosers()) {
					icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(section.getString("losers-format"), new PlaceholderUtil().add("{loser}", Bukkit.getOfflinePlayer(uuid).getName()))));
				}
				continue;
			}
			icon.appendLore(MessageUtils.parseColor(MessageUtils.applyPlaceholders(line, placeholderUtil)));
		}

		icon.setName(MessageUtils.parseColor(MessageUtils.applyPlaceholders(description.get(0), placeholderUtil)));

		return icon;
	}
}
