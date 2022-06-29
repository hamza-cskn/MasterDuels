package mc.obliviate.masterduels.api.queue;

import mc.obliviate.masterduels.queue.DuelQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelQueueJoinEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;
	private final DuelQueue queue;
	private final Player player;

	public DuelQueueJoinEvent(final DuelQueue queue, Player player) {
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


	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}
}
