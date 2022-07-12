package mc.obliviate.masterduels.commands;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DeveloperCMD implements Listener {

	private final MasterDuels plugin;

	public DeveloperCMD(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().contains("masterduels-version")) {
			event.setCancelled(true);

			event.getPlayer().sendMessage("Master Duels installed on " + Bukkit.getBukkitVersion());
			event.getPlayer().sendMessage("Server Brand: " + Bukkit.getVersion());
			event.getPlayer().sendMessage("Developed by:" + ChatColor.GREEN + " Mr_Obliviate");
			event.getPlayer().sendMessage("Version: v" + plugin.getDescription().getVersion() + " " + ChatColor.AQUA + ChatColor.ITALIC + " dev");
		} else if (event.getMessage().equalsIgnoreCase("/debug")) {
			final Player player = event.getPlayer();
			final IUser user = UserHandler.getUser(player.getUniqueId());
			player.sendMessage("Is in match builder: " + user.isInMatchBuilder());
			final Member member = UserHandler.getMember(player.getUniqueId());
			if (member == null) {
				player.sendMessage("You're not member");
				return;
			}
			player.sendMessage(member.getMatch().getAllMembers().toString());
			player.sendMessage(member.getTeam().getMembers().toString());
			player.sendMessage(member.getMatch().getGameDataStorage().getGameTeamManager().getTeams().toString());
		}
	}

}
