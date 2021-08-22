package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public DuelCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		if (DataHandler.getMember(player.getUniqueId()) != null) {
			player.sendMessage("§cYou are already in a duel.");
			return false;
		}

		if (args.length == 0) {
			player.sendMessage("§cUsage: /duel <player>");
			return false;
		}

		final String targetName = args[0];
		final Player target = Bukkit.getPlayer(targetName);

		if (target == null) {
			player.sendMessage("§cThis player is not online.");
			return false;
		}

		if (DataHandler.getMember(target.getUniqueId()) != null) {
			player.sendMessage("§cThis player is already in a duel.");
			return false;
		}

		//1v1
		final Arena arena = Arena.findArena(1, 2);

		final GameBuilder gameBuilder = Game.create(plugin, arena).teamAmount(2).teamSize(1).finishTime(60).totalRounds(1);

		gameBuilder.createTeam(player);

		gameBuilder.sendInvite(player, target, result -> {
			switch (result) {
				case EXPIRE:
				case DECLINE:

					break;
				case ACCEPT:
			}
		});

		final Game game = gameBuilder.build();

		game.startGame();


		return true;

	}


}
