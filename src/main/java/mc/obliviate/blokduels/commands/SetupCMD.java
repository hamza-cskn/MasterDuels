package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.setup.ArenaSetup;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public SetupCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		if (!player.isOp()) return false;

		try {
			final ArenaSetup setup = new ArenaSetup(plugin, player);
			Bukkit.getPluginManager().registerEvents(setup, plugin);
		} catch (IllegalStateException e) {
			player.sendMessage("You are already in a Arena setup mode.");
			return false;
		}

		player.sendMessage("§e§l- SETUPING AN ARENA -");
		player.sendMessage("§6§lINFO: §7Use blaze rod to select cuboid.");
		player.sendMessage("§6§lINFO: §7Use blaze powder to open arena setup gui.");

		return true;

	}


}
