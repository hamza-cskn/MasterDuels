package mc.obliviate.masterduels.api.queue;

import mc.obliviate.masterduels.queue.DuelQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelQueueLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final DuelQueue queue;
	private final Player player;

	public DuelQueueLeaveEvent(final DuelQueue queue, Player player) {
		this.queue = queue;
		this.player = player;
	}

	public DuelQueue getQueue() {
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
