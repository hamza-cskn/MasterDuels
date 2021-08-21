package mc.obliviate.blokduels.game;

import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.team.Team;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamBuilder {

	private final int teamAmount;
	private final int teamSize;
	private final Game game;
	private int createdTeamsAmount = 0;
	private final Map<UUID, Invite> invites = new HashMap<>();

	public TeamBuilder(int teamAmount, int teamSize, Game game) {
		this.teamAmount = teamAmount;
		this.teamSize = teamSize;
		this.game = game;
	}


	public Team create(Player... players) {
		if (players.length != teamSize) throw new IllegalArgumentException("Team size is " + teamSize + " but given " + players.length + " players.");

		if (createdTeamsAmount < teamSize) throw new IllegalArgumentException("Team amount is " + teamAmount + ". All teams has created already.");

		return new Team(++createdTeamsAmount, teamSize, Arrays.asList(players), game);

	}

	public Game getGame() {
		return game;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public int getCreatedTeamsAmount() {
		return createdTeamsAmount;
	}

	public Map<UUID, Invite> getInvites() {
		return invites;
	}

	public void removeInvite(UUID uuid) {
		invites.remove(uuid);
	}
}
