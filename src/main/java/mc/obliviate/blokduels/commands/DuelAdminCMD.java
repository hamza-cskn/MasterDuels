package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.gui.kit.KitListGUI;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.setup.ArenaSetup;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DuelAdminCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public DuelAdminCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		if (!player.isOp()) return false;

		if (args.length == 0) {
			player.sendMessage("§c§lMasterDuels §cAdministrator Commands");
			player.sendMessage(" §7/" + s + " create §8- §9enables arena setup mode");
			player.sendMessage(" §7/" + s + " arena disable <arena name> §8- §9disables an duel arena");
			player.sendMessage(" §7/" + s + " arena enable  <arena name> §8- §9enables an duel arena");
			player.sendMessage(" §7/" + s + " arena list §8- §9list arenas");
			player.sendMessage(" §7/" + s + " setlobby §8- §9sets lobby location to teleport players after duel game");
			player.sendMessage(" §7/" + s + " kitsave §8- §9saves your current inventory as new kit");
			player.sendMessage(" §7/" + s + " kiteditor §8- §9opens kit editor gui");
			player.sendMessage("");
			player.sendMessage("§nMasterDuels v" + plugin.getDescription().getVersion());
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
			SerializerUtils.serializeLocationYAML(plugin.getDatabaseHandler().getData().createSection("lobby-location"), player.getLocation());
			plugin.getDatabaseHandler().saveDataFile();
			player.sendMessage("§aLobby set successfully!");
		} else if (args[0].equalsIgnoreCase("kiteditor")) {
			new KitListGUI(player).open();
		} else if (args[0].equalsIgnoreCase("kitsave")) {
			kitSave(player, Arrays.asList(args));
		} else if (args[0].equalsIgnoreCase("arena")) {
			if (args.length == 1) {

				return false;
			}
			if (args[1].equalsIgnoreCase("disable")) {
				toggleArena(player, Arrays.asList(args), false);
			} else if (args[1].equalsIgnoreCase("enable")) {
				toggleArena(player, Arrays.asList(args), true);
			}else if (args[1].equalsIgnoreCase("list")) {

			}
		}

		return true;
	}

	private void toggleArena(Player player, List<String> args, boolean toggleState) {
		if (args.size() < 3) {
			MessageUtils.sendMessage(player, "duel-command.arena-toggle." + (toggleState ? "enable" : "disable") + "-usage");
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
		args = args.subList(1, args.size());
		final String name = String.join(" ", args);

		final Kit kit = new Kit(name, player.getInventory().getContents(), player.getInventory().getArmorContents());

		MessageUtils.sendMessage(player, "kit.has-saved", new PlaceholderUtil().add("{kit}", name));

		Kit.save(plugin, kit);
	}


}
