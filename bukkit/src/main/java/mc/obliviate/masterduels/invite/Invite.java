package mc.obliviate.masterduels.invite;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class Invite {

	private final UUID sender;
	private final UUID receiver;
	private final long expireOutTime;
	private final Consumer<Invite> response;
	private InviteState state = InviteState.PENDING;

	protected Invite(UUID sender, UUID receiverUniqueId, long expireTimeOut, Consumer<Invite> response) {
		this.sender = sender;
		this.receiver = receiverUniqueId;
		this.expireOutTime = expireTimeOut;
		this.response = response;

		InviteRecipient.getInviteRecipient(receiverUniqueId).addInvite(this);

		Bukkit.getScheduler().runTaskLater(MasterDuels.getInstance(), () -> {
			if (state.equals(InviteState.PENDING)) {
				response(InviteState.EXPIRED);
			}
		}, (expireTimeOut - System.currentTimeMillis()) / 50); //time difference in ms / 50 = time difference in ticks
	}

	/**
	 * Purpose of this class is,
	 * building new invite class.
	 */
	public static class Builder {

		private UUID sender;
		private UUID receiver;
		private long expireTime;
		private Consumer<Invite> response;

		protected Builder() {
		}

		public InviteBuildResult build() {
			InviteRecipient inviteRecipient = InviteRecipient.getInviteRecipient(receiver);
			for (Invite invite : new ArrayList<>(inviteRecipient.getInvites())) {
				if (invite.sender.equals(sender)) {
					if (invite.expireOutTime < System.currentTimeMillis()) {
						return new InviteBuildResult(null, InviteBuildState.ERROR_ALREADY_INVITED);
					} else {
						inviteRecipient.removeInvite(invite);
					}
				}
			}

			final Invite inviteBuilt = new Invite(sender, receiver, expireTime, response);
			return new InviteBuildResult(inviteBuilt, InviteBuildState.SUCCESS);
		}

		public Consumer<Invite> getResponse() {
			return response;
		}

		public long getExpireTime() {
			return expireTime;
		}

		public UUID getReceiver() {
			return receiver;
		}

		public UUID getSender() {
			return sender;
		}

		public Builder onResponse(Consumer<Invite> action) {
			Preconditions.checkArgument(receiver != null, "action cannot be null");
			this.response = action;
			return this;
		}

		public Builder setExpireTimeLater(long msLater) {
			Preconditions.checkArgument(msLater > 0, "expire time cannot be negative");
			return setExpireTime(System.currentTimeMillis() + msLater);
		}

		public Builder setExpireTime(long expireTime) {
			Preconditions.checkArgument(expireTime > System.currentTimeMillis(), "expire time cannot be smaller than now");
			this.expireTime = expireTime;
			return this;
		}

		public Builder setReceiver(UUID receiver) {
			Preconditions.checkArgument(receiver != null, "receiver cannot be null");
			this.receiver = receiver;
			return this;
		}

		public Builder setSender(UUID sender) {
			Preconditions.checkArgument(sender != null, "sender cannot be null");
			this.sender = sender;
			return this;
		}
	}

	public static Builder create() {
		return new Builder();
	}

	public void response(InviteState inviteState) {
		state = inviteState;
		InviteRecipient.getInviteRecipient(receiver).removeInvite(this);
		response.accept(this);
	}

	public InviteState getState() {
		return state;
	}

	public long getExpireOutTime() {
		return expireOutTime;
	}

	public UUID getRecipientUniqueId() {
		return receiver;
	}

	public UUID getSenderUniqueId() {
		return sender;
	}


	/**
	 * Purpose of this class is,
	 * storing invite and invite build state
	 * when invite builder built.
	 */
	public static class InviteBuildResult {
		final Invite invite;
		final InviteBuildState inviteBuildState;

		public InviteBuildResult(Invite invite, InviteBuildState inviteBuildState) {
			this.invite = invite;
			this.inviteBuildState = inviteBuildState;
		}

		public Invite getInvite() {
			return invite;
		}

		public InviteBuildState getInviteBuildState() {
			return inviteBuildState;
		}
	}

	/**
	 * Purpose of this class is,
	 * defining build result of invite.
	 */
	public enum InviteBuildState {
		SUCCESS,
		ERROR_ALREADY_INVITED,
	}

	/**
	 * Purpose of this class is,
	 * defining current state of invite.
	 */
	public enum InviteState {

		PENDING,
		ACCEPTED,
		REJECTED,
		EXPIRED,
		CANCELLED
	}

}
