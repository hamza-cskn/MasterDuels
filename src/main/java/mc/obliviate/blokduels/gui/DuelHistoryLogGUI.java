package mc.obliviate.blokduels.gui;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.history.GameHistoryLog;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DuelHistoryLogGUI extends GUI {

	public DuelHistoryLogGUI(Player player) {
		super(player, "duel-history-log-gui", "Duel History", 6);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		fillRow(new Icon(Material.STAINED_GLASS_PANE).setDamage(15), 0);

		int slot = 9;
		//todo clean here
		//todo may wanna cache?
		final List<GameHistoryLog> logs;
		try {
			logs = ((BlokDuels) getPlugin()).getSqlManager().getAllLogs();
		} catch (final SQLException e) {
			e.printStackTrace();
			player.sendMessage("An error occurred. Please contact to an administrator or developer.");
			return;
		}
		for (final GameHistoryLog log : logs) {

			final Icon icon = new Icon(Material.STONE_SWORD)
					.setName(ChatColor.AQUA + "Game Time: " + ChatColor.YELLOW + TimerUtils.getFormattedDifferentTime(log.getStartTime(), log.getEndTime()))
					.setLore("", ChatColor.DARK_GREEN + " Winners: ");
			for (final UUID uuid : log.getWinners()) {
				icon.appendLore(ChatColor.GRAY + "  - " + ChatColor.GREEN + Bukkit.getOfflinePlayer(uuid).getName());
			}

			icon.appendLore("", ChatColor.DARK_RED + " Losers: ");

			for (final UUID uuid : log.getLosers()) {
				icon.appendLore(ChatColor.GRAY + "  - " + ChatColor.RED + Bukkit.getOfflinePlayer(uuid).getName());
			}

			addItem(slot++, icon);
		}
	}
}
