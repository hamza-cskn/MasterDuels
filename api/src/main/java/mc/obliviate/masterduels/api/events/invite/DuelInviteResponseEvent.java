package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;
import mc.obliviate.masterduels.api.invite.InviteState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;
import java.util.function.Consumer;

public class DuelInviteResponseEvent extends Event implements InviteEvent {

	private static final HandlerList handlers = new HandlerList();
	private final IInvite invite;
	private final Consumer<InviteState> response;

	public DuelInviteResponseEvent(IInvite invite, Consumer<InviteState> response) {
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

	public UUID getSenderUniqueId() {
		return invite.getSenderUniqueId();
	}

	public UUID getRecipientUniqueId() {
		return invite.getRecipientUniqueId();
	}

	public Consumer<InviteState> getResponse() {
		return response;
	}
}
