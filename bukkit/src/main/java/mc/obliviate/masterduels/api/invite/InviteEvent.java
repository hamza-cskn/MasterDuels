package mc.obliviate.masterduels.api.invite;

import mc.obliviate.masterduels.invite.Invite;

import java.util.UUID;

public interface InviteEvent {

	Invite getInvite();

	default UUID getSenderUniqueId() {
		return getInvite().getSenderUniqueId();
	}

	default UUID getRecipientUniqueId() {
		return getInvite().getRecipientUniqueId();
	}

}
