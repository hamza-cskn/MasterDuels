package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;
import mc.obliviate.masterduels.api.invite.InviteResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Consumer;

public class DuelInviteResponseEvent extends Event implements InviteEvent {

	private static final HandlerList handlers = new HandlerList();
	private final IInvite invite;
	private final Consumer<InviteResult> response;

	public DuelInviteResponseEvent(IInvite invite, Consumer<InviteResult> response) {
		this.invite = invite;
		this.response = response;
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

	public Consumer<InviteResult> getResponse() {
		return response;
	}
}
