package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInviteSentEvent extends Event implements InviteEvent, Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final IInvite invite;
	private boolean isCancelled = false;

	public DuelInviteSentEvent(IInvite invite) {
		this.invite = invite;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IInvite getInvite() {
		return invite;
	}

	public Player getSender() {
		return invite.getInviter();
	}

	public Player getReceiver() {
		return invite.getTarget();
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;
	}
}
