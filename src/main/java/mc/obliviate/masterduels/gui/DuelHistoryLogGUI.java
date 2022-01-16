package mc.obliviate.masterduels.gui;

import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DuelHistoryLogGUI extends GUI {

	public static ConfigurationSection guiSection;

	public DuelHistoryLogGUI(Player player) {
		super(player, "duel-history-log-gui", "Title could not loaded", 6);
		setTitle(guiSection.getString("title", "Title could not loaded"));
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()), 0);

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

	private Icon deserializeIcon(final ConfigurationSection section, GameHistoryLog log) { //todo make config storage class to store itemstacks in cache
		if (section == null)
			return new Icon(XMaterial.BEDROCK.parseItem()).setName("Item could not deserialized. (Config Section is null)");
		final Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(section.getString("material-type"));

		if (!xMaterial.isPresent())
			return new Icon(XMaterial.BEDROCK.parseItem()).setName("Item could not deserialized.");
		final Icon icon = new Icon(xMaterial.get().parseItem());
		final List<String> description = section.getStringList("description");

		final PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{time}", TimerUtils.getFormattedDifferentTime(log.getStartTime(), log.getEndTime()))
				.add("{played-date}", new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date(log.getStartTime())));
		if (log.getWinners().size() == 1) {
			placeholderUtil.add("{winner}", Bukkit.getOfflinePlayer(log.getWinners().get(0)).getName());
		}
		if (log.getLosers().size() == 1) {
			placeholderUtil.add("{loser}", Bukkit.getOfflinePlayer(log.getLosers().get(0)).getName());
		}
		for (final String line : description.subList(1, description.size())) {
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
