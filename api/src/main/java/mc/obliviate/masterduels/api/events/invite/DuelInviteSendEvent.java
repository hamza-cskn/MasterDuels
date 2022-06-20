package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInviteSendEvent extends Event implements InviteEvent, Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final IInvite invite;
	private boolean isCancelled = false;

	public DuelInviteSendEvent(IInvite invite) {
		this.invite = invite;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IInvite getInvite() {
		return invite;
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
