package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.setup.chatentry.ChatEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		ChatEntry.trigger(e);
	}

}
