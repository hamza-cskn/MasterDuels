package mc.obliviate.masterduels.game;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamBuilderManager {

	private final Map<Integer, TeamBuilder> teams = new HashMap<>();
	private final GameBuilder builder;

	protected TeamBuilderManager(final GameBuilder builder) {
		this.builder = builder;
	}

	public void registerTeamsIntoGame(final Game game) {
		for (final TeamBuilder teamBuilder : teams.values()) {
			game.registerTeam(teamBuilder.build(game));
		}
	}

	public GameBuilder getBuilder() {
		return builder;
	}

	public Map<Integer, TeamBuilder> getTeams() {
		return teams;
	}

	public int getTeamAmount() {
		return builder.getTeamAmount();
	}

	public void setTeamAmount(final int teamAmount) {
		builder.setTeamAmount(teamAmount);
	}

	public int getTeamSize() {
		return builder.getTeamSize();
	}

	public void setTeamSize(final int teamSize) {
		builder.setTeamSize(teamSize);
	}

	public TeamBuilder registerNewTeam() {
		return registerNewTeam(null);
	}

	public TeamBuilder registerNewTeam(final List<Player> players) {
		if (teams.size() > getTeamAmount()) {
			throw new IllegalStateException("Team amount limit is " + getTeamAmount() + ". All teams has created already. Team amount is " + teams.size());
		}

		final int id = teams.size() + 1;
		final TeamBuilder teamBuilder = new TeamBuilder(this, players, id);
		teams.put(id, teamBuilder);
		return teamBuilder;
	}

}
