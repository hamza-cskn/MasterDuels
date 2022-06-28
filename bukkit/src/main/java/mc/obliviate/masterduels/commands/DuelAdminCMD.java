package mc.obliviate.masterduels.commands;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.kit.gui.KitListEditorGUI;
import mc.obliviate.masterduels.setup.ArenaSetup;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class DuelAdminCMD implements CommandExecutor {

	private final MasterDuels plugin;

	public DuelAdminCMD(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdStr, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		if (!player.isOp()) return false;

		if (args.length == 0) {
			MessageUtils.sendMessage(player, "duel-command.admin.usage");
			return false;
		}

		if (args[0].equalsIgnoreCase("create")) {

			try {
				final ArenaSetup setup = new ArenaSetup(plugin, player);
				Bukkit.getPluginManager().registerEvents(setup, plugin);
			} catch (IllegalStateException e) {
				player.sendMessage("§cYou are already in a Arena setup mode.");
				return false;
			}

			player.sendMessage("§e§l- SETUPING AN ARENA -");
			player.sendMessage("§6§lINFO: §7Use blaze rod to select cuboid.");
			player.sendMessage("§6§lINFO: §7Use blaze powder to open arena setup gui.");
			player.sendMessage("§6§lINFO: §7Selected blocks will be redstone block at client-side");

			player.getInventory().addItem(new ItemStack(Material.BLAZE_POWDER));
			player.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
			return true;

		} else if (args[0].equalsIgnoreCase("setlobby")) {
			SerializerUtils.serializeLocationYAML(plugin.getConfigurationHandler().getData().createSection("lobby-location"), player.getLocation());
			plugin.getConfigurationHandler().saveDataFile();
			MessageUtils.sendMessage(player, "duel-command.admin.lobby-set");

		} else if (args[0].equalsIgnoreCase("kit")) {
			if (args.length == 1) {
				MessageUtils.sendMessage(player, "kit.usage");
				return false;
			}
			if (args[1].equalsIgnoreCase("editor")) {
				new KitListEditorGUI(player).open();
			} else if (args[1].equalsIgnoreCase("save")) {
				kitSave(player, Arrays.asList(args));
			}

		} else if (args[0].equalsIgnoreCase("arena")) {
			if (args.length == 1) {
				MessageUtils.sendMessage(player, "duel-command.arena.usage");
				return false;
			}
			if (args[1].equalsIgnoreCase("disable")) {
				toggleArena(player, Arrays.asList(args), false);
			} else if (args[1].equalsIgnoreCase("enable")) {
				toggleArena(player, Arrays.asList(args), true);
			} else if (args[1].equalsIgnoreCase("cancel")) {
				cancelCmd(player, Arrays.asList(args));
			}
			return true;

		} else if (args[0].equalsIgnoreCase("teststart")) {
			testStart(player, Arrays.asList(args));
			return true;

		}
		return true;
	}

	private void cancelCmd(final Player player, final List<String> args) {
		if (args.size() == 2) {
			//dueladmin arena cancel arena1 -all
			MessageUtils.sendMessage(player, "");
		} else if (args.size() == 3) {
			cancelGame(DataHandler.getArenaFromName(args.get(2)));
		} else if (args.get(3).equalsIgnoreCase("-all")) {
			for (final Arena arena : DataHandler.getArenas().keySet()) {
				cancelGame(arena);
			}
		}
	}

	private void cancelGame(Arena arena) {
		Match game = DataHandler.getArenas().get(arena);
		if (game == null) return;
		game.uninstall();
	}

	private void testStart(final Player player, final List<String> args) {
		if (args.size() != 3) {
			player.sendMessage("§cWrong usage! §7/dueladmin teststart <player 1> <player 2>");
			return;
		}

		final Player p1 = Bukkit.getPlayerExact(args.get(1));
		final Player p2 = Bukkit.getPlayerExact(args.get(2));
		if (p1 == null || p2 == null) {
			player.sendMessage("§cA player is not online.");
			return;
		}

		final MatchBuilder gameBuilder = new MatchBuilder().setTeamSize(1).setTeamAmount(2).setMatchDuration(Duration.ofSeconds(1440));
		gameBuilder.addPlayer(p1);
		gameBuilder.addPlayer(p2);

		final IMatch game = gameBuilder.build();
		if (game != null) {
			game.start();
		} else {
			MessageUtils.sendMessage(player, "no-arena-found");
		}
	}


	private void toggleArena(Player player, List<String> args, boolean toggleState) {
		if (args.size() < 3) {
			MessageUtils.sendMessage(player, "duel-command.arena.toggle.usage." + (toggleState ? "enable" : "disable"));
			return;
		}

		final Arena arena = DataHandler.getArenaFromName(args.get(2));
		if (arena == null) {
			MessageUtils.sendMessage(player, "duel-command.no-arena-found-with-this-name");
			return;
		}

		arena.setEnabled(toggleState);
		MessageUtils.sendMessage(player, "duel-command.arena-toggle." + (toggleState ? "enabled" : "disabled"));

	}

	private void kitSave(Player player, List<String> args) {
		if (args.size() < 2) {
			MessageUtils.sendMessage(player, "kit.editor-usage");
			return;
		}
		args = args.subList(2, args.size());
		final String name = String.join(" ", args);

		final Kit kit = new Kit(name, player.getInventory().getContents(), player.getInventory().getArmorContents());

		MessageUtils.sendMessage(player, "kit.has-saved", new PlaceholderUtil().add("{kit}", name));

		Kit.save(plugin, kit);
	}


}
