package mc.obliviate.blokduels.bukkit.commands;

import mc.obliviate.blokduels.bukkit.BlokDuels;
import mc.obliviate.blokduels.bukkit.gui.DuelArenaListGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelArenasCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public DuelArenasCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		new DuelArenaListGUI(player).open();

		return true;

	}


}
