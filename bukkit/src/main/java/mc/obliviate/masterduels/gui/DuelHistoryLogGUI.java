package mc.obliviate.masterduels.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.masterduels.history.MatchHistoryLog;
import mc.obliviate.masterduels.history.PlayerHistoryLog;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static mc.obliviate.masterduels.data.SQLManager.loadDuelHistories;

public class DuelHistoryLogGUI extends ConfigurableGui {

	public DuelHistoryLogGUI(Player player) {
		super(player, "duel-history-log-gui");
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		List<MatchHistoryLog> logs = loadDuelHistories(); //todo cache it
		int i = 9;
		for (MatchHistoryLog log : logs) {
			Icon icon = new Icon(Material.TNT).setAmount(i - 8).setName(ChatColor.DARK_PURPLE + TimerUtils.formatDate(log.getStartTime()))
					.appendLore(ChatColor.GRAY + "Duel Time: " + ChatColor.LIGHT_PURPLE + TimerUtils.formatTimeDifferenceAsTimer(log.getStartTime(), log.getFinishTime()))
					.appendLore(ChatColor.WHITE.toString() + log.getPlayedRound() + ChatColor.GRAY + "/" + log.getMaxRound() + " rounds played.")
					.appendLore("");
			for (Map.Entry<UUID, PlayerHistoryLog> entry : log.getPlayerHistoryLogMap().entrySet()) {
				if (log.getWinners().contains(entry.getKey()))
					icon.appendLore(ChatColor.GREEN + Utils.getDisplayName(Bukkit.getOfflinePlayer(entry.getKey())) + ChatColor.YELLOW + ChatColor.BOLD + " WINNER");
				else
					icon.appendLore(ChatColor.RED + Utils.getDisplayName(Bukkit.getOfflinePlayer(entry.getKey())));
				icon.appendLore(ChatColor.GRAY + "Kit: " + ChatColor.WHITE + entry.getValue().getKitName());
				icon.appendLore(ChatColor.GRAY + "Damage Dealt: " + ChatColor.WHITE + entry.getValue().getDamageDealt() / 5d + ChatColor.RED + "❤");
				icon.appendLore(ChatColor.GRAY + "Bow Accuracy: " + ChatColor.WHITE + MessageUtils.getPercentage(entry.getValue().getArrow().getThrew(), entry.getValue().getArrow().getHit()) + "%");
				icon.appendLore("");
			}
			addItem(i++, icon);
		}
	}

	@Override
	public String getSectionPath() {
		return "game-history-gui";
	}
}
