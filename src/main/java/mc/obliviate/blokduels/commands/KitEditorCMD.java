package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.gui.kit.KitListGUI;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.kit.serializer.KitSerializer;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class KitEditorCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public KitEditorCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		if (!player.isOp()) return false;
		if (args.length == 0) {
			MessageUtils.sendMessage(player, "kit.editor-usage");
			return false;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("menu")) {
				new KitListGUI(player).open();
			} else {
				MessageUtils.sendMessage(player, "kit.editor-usage");
				return false;
			}
		}

		if (args[0].equalsIgnoreCase("save")) {
			kitSave(player, Arrays.asList(args));
		}

		return true;
	}

	private void kitSave(Player player, List<String> args) {
		args = args.subList(1, args.size());
		final String name = String.join(" ", args);

		final Kit kit = new Kit(name, player.getInventory().getContents(), player.getInventory().getArmorContents());

		MessageUtils.sendMessage(player, "kit.has-saved", new PlaceholderUtil().add("{kit}", name));

		Kit.save(plugin,kit);
	}


}
