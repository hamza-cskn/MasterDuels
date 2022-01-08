package mc.obliviate.masterduels.setup.chatentry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatEntry {

	private static final Map<UUID, ChatEntry> entryMap = new HashMap<>();
	private final Plugin plugin;
	public Entry entry;

	public ChatEntry(UUID uuid, Plugin plugin) {
		this.plugin = plugin;
		entryMap.put(uuid, this);
	}

	public static void trigger(AsyncPlayerChatEvent e) {
		final Player sender = e.getPlayer();
		final ChatEntry chatEntry = entryMap.get(sender.getUniqueId());
		if (chatEntry == null || chatEntry.getEntry() == null) return;
		e.setCancelled(true);
		Bukkit.getScheduler().runTask(chatEntry.plugin, () -> {
			chatEntry.getEntry().entry(e);
		});
		entryMap.remove(sender.getUniqueId());

	}

	public void onResponse(Entry entry) {
		this.entry = entry;
	}

	public Entry getEntry() {
		return entry;
	}

}
