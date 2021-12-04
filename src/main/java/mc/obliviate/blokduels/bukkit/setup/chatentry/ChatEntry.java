package mc.obliviate.blokduels.bukkit.setup.chatentry;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatEntry {

	private static final Map<UUID, ChatEntry> entryMap = new HashMap<>();

	public Entry entry;

	public ChatEntry(UUID uuid) {
		entryMap.put(uuid, this);
	}

	public void onResponse(Entry entry) {
		this.entry = entry;
	}

	public Entry getEntry() {
		return entry;
	}

	public static void trigger(AsyncPlayerChatEvent e) {
		final Player sender = e.getPlayer();
		final ChatEntry chatEntry = entryMap.get(sender.getUniqueId());
		if (chatEntry == null || chatEntry.getEntry() == null) return;
		e.setCancelled(true);
		chatEntry.getEntry().entry(e);
		entryMap.remove(sender.getUniqueId());

	}

}
