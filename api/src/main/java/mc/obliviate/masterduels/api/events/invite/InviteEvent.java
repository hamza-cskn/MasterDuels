package mc.obliviate.masterduels.api.events.invite;

import mc.obliviate.masterduels.api.invite.IInvite;

import java.util.UUID;

public interface InviteEvent {

	IInvite getInvite();

	default UUID getSenderUniqueId() {
		return getInvite().getSenderUniqueId();
	}

	default UUID getRecipientUniqueId() {
		return getInvite().getRecipientUniqueId();
	}

}
