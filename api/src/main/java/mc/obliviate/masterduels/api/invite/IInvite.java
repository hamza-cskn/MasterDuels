package mc.obliviate.masterduels.api.invite;

import java.util.UUID;

public interface IInvite {

	void response(InviteState inviteState);

	InviteState getState();

	long getExpireOutTime();

	UUID getRecipientUniqueId();

	UUID getSenderUniqueId();
}
