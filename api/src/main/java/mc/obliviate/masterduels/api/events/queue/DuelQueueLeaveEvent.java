package mc.obliviate.masterduels.api.events.queue;

import mc.obliviate.masterduels.api.queue.IDuelQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelQueueLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final IDuelQueue queue;
	private final Player player;

	public DuelQueueLeaveEvent(final IDuelQueue queue, Player player) {
		this.queue = queue;
		this.player = player;
	}

	public IDuelQueue getQueue() {
		return queue;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
