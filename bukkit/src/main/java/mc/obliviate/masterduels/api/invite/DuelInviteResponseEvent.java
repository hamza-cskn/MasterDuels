package mc.obliviate.masterduels.api.invite;

import mc.obliviate.masterduels.invite.Invite;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInviteResponseEvent extends Event implements InviteEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Invite invite;

	public DuelInviteResponseEvent(Invite invite) {
		this.invite = invite;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Invite getInvite() {
		return invite;
	}

}
