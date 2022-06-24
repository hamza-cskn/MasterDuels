package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInviteResponseEvent extends Event implements InviteEvent {

	private static final HandlerList handlers = new HandlerList();
	private final IInvite invite;

	public DuelInviteResponseEvent(IInvite invite) {
		this.invite = invite;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IInvite getInvite() {
		return invite;
	}

}
