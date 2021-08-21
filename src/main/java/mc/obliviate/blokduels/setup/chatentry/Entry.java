package mc.obliviate.blokduels.setup.chatentry;

import org.bukkit.event.player.AsyncPlayerChatEvent;

@FunctionalInterface
public interface Entry {

	void entry(AsyncPlayerChatEvent e);

}

