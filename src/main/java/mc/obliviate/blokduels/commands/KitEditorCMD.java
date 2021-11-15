package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.kit.Kit;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			MessageUtils.sendMessage(player, "kit-save-usage");
			return false;
		}

		final String name = String.join(" ", args);

		new Kit(name, player.getInventory().getContents(), player.getInventory().getArmorContents());

		MessageUtils.sendMessage(player, "kit-has-saved", new PlaceholderUtil().add("{kit}", name));

		return true;
	}


}
