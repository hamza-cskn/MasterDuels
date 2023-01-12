package mc.obliviate.masterduels.setup.chatentry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatEntry {

	private static final Map<UUID, ChatEntry> entryMap = new HashMap<>();
	private final Plugin plugin;
	public Consumer<AsyncPlayerChatEvent> action;

	public ChatEntry(UUID uuid, Plugin plugin) {
		this.plugin = plugin;
		entryMap.put(uuid, this);
	}

	public static void trigger(AsyncPlayerChatEvent e) {
		final Player sender = e.getPlayer();
		final ChatEntry chatEntry = entryMap.get(sender.getUniqueId());
		if (chatEntry == null || chatEntry.getAction() == null) return;
		e.setCancelled(true);
        Bukkit.getScheduler().runTask(chatEntry.plugin, () -> chatEntry.getAction().accept(e));
        unregisterEntryTask(sender.getUniqueId());
	}

	public static void unregisterEntryTask(UUID senderUniqueId) {
		entryMap.remove(senderUniqueId);
	}

	public void onResponse(Consumer<AsyncPlayerChatEvent> e) {
		this.action = e;
	}

	public Consumer<AsyncPlayerChatEvent> getAction() {
		return action;
	}

}
