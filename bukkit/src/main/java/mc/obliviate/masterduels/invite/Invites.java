package mc.obliviate.masterduels.invite;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Invites {

	private final UUID playerUniqueId;
	private final List<Invite> invites = new ArrayList<>();

	public Invites(final UUID uuid) {
		this.playerUniqueId = uuid;
	}

	public void add(final Invite invite) {
		invites.add(invite);
	}

	public boolean removeInvite(final Invite invite) {
		invites.remove(invite);
		return invites.size() == 0;
	}

	public UUID getPlayerUniqueId() {
		return playerUniqueId;
	}

	public int size() {
		return invites.size();
	}

	public Invite get(int i) {
		return invites.get(i);
	}

	public List<Invite> getInvites() {
		return invites;
	}
}