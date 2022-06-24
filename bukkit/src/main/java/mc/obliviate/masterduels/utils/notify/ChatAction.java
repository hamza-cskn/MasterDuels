package mc.obliviate.masterduels.utils.notify;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatAction implements NotifyAction {

	private final String message;

	public ChatAction(String message) {
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}

	@Override
	public void run(Player player) {
		player.sendMessage(message);
	}
}
