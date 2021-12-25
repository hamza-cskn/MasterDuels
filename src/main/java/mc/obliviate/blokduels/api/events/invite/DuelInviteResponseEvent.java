package mc.obliviate.blokduels.api.events.invite;

import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.invite.InviteResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInviteResponseEvent extends Event implements InviteEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Invite invite;
	private final InviteResponse response;

	public DuelInviteResponseEvent(Invite invite, InviteResponse response) {
		this.invite = invite;
		this.response = response;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Invite getInvite() {
		return invite;
	}

	public Player getSender() {
		return invite.getInviter();
	}

	public Player getReceiver() {
		return invite.getTarget();
	}

	public InviteResponse getResponse() {
		return response;
	}
}
