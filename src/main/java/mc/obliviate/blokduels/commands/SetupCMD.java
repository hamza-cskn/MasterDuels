package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.setup.ArenaSetup;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

		if (args.length == 0) {
			player.sendMessage("§6Available commands:");
			player.sendMessage("/" + s + " §ecreate");
			player.sendMessage("/" + s + " §esetlobby");
			player.sendMessage("/" + s + " §egamestate");
			return false;
		}

		if (args[0].equalsIgnoreCase("create")) {

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

			player.getInventory().addItem(new ItemStack(Material.BLAZE_POWDER));
			player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
			return true;

		} else if (args[0].equalsIgnoreCase("setlobby")) {
			SerializerUtils.serializeLocation(plugin.getDatabaseHandler().getData().createSection("lobby-location"), player.getLocation());
			plugin.getDatabaseHandler().saveDataFile();
			player.sendMessage("§aLobby has set!");
		} else if (args[0].equalsIgnoreCase("gamestate")) {
			final Member member = DataHandler.getMember(player.getUniqueId());
			if (member == null) {
				player.sendMessage("You are not in a game.");
				return false;
			}

			player.sendMessage(member.getTeam().getGame().getGameState().toString());
		} else if (args[0].equalsIgnoreCase("testdoubles")) {

			final Arena arena = Arena.findArena(2, 2);

			if (arena == null) {
				player.sendMessage("§cCould not found any available arena.");
				return false;
			}

			final GameBuilder gameBuilder = Game.create(plugin, arena).teamAmount(2).teamSize(2).finishTime(60).totalRounds(1);

			gameBuilder.createTeam(player, Bukkit.getPlayer("KillsGames99"));
			gameBuilder.createTeam(Bukkit.getPlayer("MHF_Squid"), Bukkit.getPlayer("BeachKills99"));
			gameBuilder.build().startGame();
		}
			return true;
	}


}
