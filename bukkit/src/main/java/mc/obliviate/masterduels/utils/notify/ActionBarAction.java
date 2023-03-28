package mc.obliviate.masterduels.utils.notify;

import com.hakan.core.HCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBarAction implements NotifyAction {

	private final String message;

	public ActionBarAction(String message) {
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}

	@Override
	public void run(Player player) {
		HCore.sendActionBar(player, message);
	}
}
