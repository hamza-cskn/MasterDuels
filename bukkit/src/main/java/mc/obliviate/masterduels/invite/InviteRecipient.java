package mc.obliviate.masterduels.invite;

import java.util.*;

/**
 * Purpose of this class is,
 * storing all invites thats is sent to
 * a player.
 */
public class InviteRecipient {

	private static final Map<UUID, InviteRecipient> inviteReceivers = new HashMap<>();

	private final List<Invite> invites = new ArrayList<>();

	private InviteRecipient(UUID receiverUniqueId) {
		inviteReceivers.put(receiverUniqueId, this);
	}

	public static InviteRecipient getInviteRecipient(UUID uuid) {
		InviteRecipient receiver = inviteReceivers.get(uuid);
		if (receiver != null) return receiver;
		return new InviteRecipient(uuid);
	}

	protected void addInvite(Invite invite) {
		invites.add(invite);
	}

	protected void removeInvite(Invite invite) {
		invites.remove(invite);
	}

	public List<Invite> getInvites() {
		return invites;
	}
}
