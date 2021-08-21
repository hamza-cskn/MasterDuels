package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.TeamBuilder;
import mc.obliviate.blokduels.game.types.GameMode;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

		final Game game = Game.create(plugin, 1, arena, new Kit(null), 60, new ArrayList<>());

		if (game == null) {
			player.sendMessage("§cGame couldn't started.");
			return false;
		}

		final TeamBuilder teamBuilder = game.getTeamBuilder();

		teamBuilder.create(player);
		teamBuilder.create(target);

		game.startGame();


		return true;

	}


}
