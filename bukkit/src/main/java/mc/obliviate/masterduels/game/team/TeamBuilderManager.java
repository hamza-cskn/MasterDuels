package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.api.arena.ITeamBuilder;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class TeamBuilderManager {

	private final Map<Integer, ITeamBuilder> teams = new HashMap<>();
	private final GameBuilder builder;

	public TeamBuilderManager(final GameBuilder builder) {
		this.builder = builder;
	}

	public void registerTeamsIntoGame(final Game game) {
		for (final ITeamBuilder teamBuilder : teams.values()) {
			registerTeamIntoGame(teamBuilder, game);
		}
	}

	private void registerTeamIntoGame(ITeamBuilder builder, Game game) {
		game.getGameDataStorage().getGameTeamManager().registerTeam(builder.build(game));
	}

	public GameBuilder getBuilder() {
		return builder;
	}

	public Map<Integer, ITeamBuilder> getTeams() {
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

	public ITeamBuilder registerNewTeam() {
		return registerNewTeam(null);
	}

	public ITeamBuilder registerNewTeam(final List<Player> players) {
		if (teams.size() > getTeamAmount()) {
			throw new IllegalStateException("Team amount limit is " + getTeamAmount() + ". All teams has created already. Team amount is " + teams.size());
		}

		final int id = teams.size() + 1;
		final ITeamBuilder teamBuilder = new TeamBuilder(this, players, id);
		teams.put(id, teamBuilder);
		return teamBuilder;
	}

}
