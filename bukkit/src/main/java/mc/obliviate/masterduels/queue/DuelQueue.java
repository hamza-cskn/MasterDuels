package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.api.queue.DuelQueueJoinEvent;
import mc.obliviate.masterduels.api.queue.DuelQueueLeaveEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelQueue {

	private boolean locked = false;

	private static final Map<DuelQueueTemplate, DuelQueue> availableQueues = new HashMap<>();
	private final DuelQueueTemplate template;
	private final MatchBuilder builder;

	DuelQueue(final DuelQueueTemplate template, final MatchBuilder builder) {
		this.builder = builder;
		this.template = template;
		final DuelQueue queue = availableQueues.get(template);
		if (queue != null) {
			queue.lock();
			Logger.severe("double queue creation found in same template.");
		}
		availableQueues.put(template, this);
	}

	public static Map<DuelQueueTemplate, DuelQueue> getAvailableQueues() {
		return availableQueues;
	}

	public static DuelQueue findQueueOfPlayer(Player player) { //fixme change that usage
		for (DuelQueue queue : availableQueues.values()) {
			if (queue.builder.getPlayers().contains(player.getUniqueId())) return queue;
		}
		return null;
	}

	public MatchBuilder getBuilder() {
		return builder;
	}

	public void addPlayer(final Player player) {
		final DuelQueueJoinEvent event = new DuelQueueJoinEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return; //api cancel

		builder.addPlayer(player, template.getKit());
		if (builder.getPlayers().size() == builder.getTeamSize() * builder.getTeamAmount()) {
			start();
		}
	}

	public void removePlayer(final Player player) {
		Bukkit.getPluginManager().callEvent(new DuelQueueLeaveEvent(this, player));
		builder.removePlayer(player);
	}

	public void start() {
		final Match game = builder.build(template.getAllowedMaps());
		if (game == null) {
			for (final UUID uuid : builder.getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null) continue;
				MessageUtils.sendMessage(player, "queue.queue-could-not-started");
			}
			return;
		}
		lock();
		game.start();
	}

	/**
	 * when a queue locked, any player cannot join/leave the queue.
	 * queue leaves from available duel queue list.
	 */
	public void lock() {
		locked = true;
		availableQueues.remove(template);
		template.createNewQueue();
	}

	public boolean isLocked() {
		return locked;
	}

	public String getName() {
		return template.getName();
	}
}