package mc.obliviate.blokduels.team;

import mc.obliviate.blokduels.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private final int teamId;
	private final int size;
	private final List<Member> members = new ArrayList<>();
	private final Game game;

	public Team(final int teamId, final int size, final List<Player> members, final Game game) {
		this.teamId = teamId;
		this.size = size;
		this.game = game;

		Bukkit.broadcastMessage("members creating");
		members.forEach(p -> this.members.add(new Member(this, p)));
	}

	public Game getGame() {
		return game;
	}

	public int getSize() {
		return size;
	}

	public List<Member> getMembers() {
		return new ArrayList<>(members);
	}

	public void removeMember(Member member) {
		members.remove(member);
	}

	public int getTeamId() {
		return teamId;
	}
}
