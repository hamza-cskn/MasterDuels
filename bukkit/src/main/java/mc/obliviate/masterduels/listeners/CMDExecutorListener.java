package mc.obliviate.masterduels.listeners;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Purpose of this class,
 * executes console commands which is given in config.yml
 **/
public class CMDExecutorListener implements Listener {

	@EventHandler
	public void onDuelGameStart(DuelMatchStartEvent event) {
		for (String cmd : ConfigurationHandler.getConfig().getStringList("execute-console-commands.when-duel-started")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		Player player = Bukkit.getPlayerExact("Mr_Obliviate");
		if (player == null) return;
		player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "TYPE"
				+ ChatColor.GOLD + ChatColor.BOLD + " MEMBERS AMOUNT"
				+ ChatColor.GREEN + ChatColor.BOLD + " TEAMS AMOUNT"
				+ ChatColor.AQUA + ChatColor.BOLD + " REMAINING TIME"
				+ ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " USER TYPE");
		event.getMatch().getGameTaskManager().repeatTask("debug", () -> {
			IUser user = UserHandler.getUser(player.getUniqueId());
			HCore.sendActionBar(player, ChatColor.RED + ChatColor.BOLD.toString() + event.getMatch().getMatchState().getMatchStateType()
					+ ChatColor.GOLD + ChatColor.BOLD + " " + event.getMatch().getAllMembers().size()
					+ ChatColor.GREEN + ChatColor.BOLD + " " + event.getMatch().getGameDataStorage().getGameTeamManager().getTeams().size()
					+ ChatColor.AQUA + ChatColor.BOLD + " " + TimerUtils.formatTimeUntilThenAsTimer(event.getMatch().getGameDataStorage().getFinishTime())
					+ ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " " + user.getClass().getSimpleName()
			);
		}, null, 0, 1);
	}

	@EventHandler
	public void onDuelGameEnd(DuelMatchEndEvent event) {
		for (String cmd : ConfigurationHandler.getConfig().getStringList("execute-console-commands.when-duel-ended")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}


}
