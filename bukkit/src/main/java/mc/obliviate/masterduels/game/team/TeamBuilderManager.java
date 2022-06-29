package mc.obliviate.masterduels.game.team;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class TeamBuilderManager {

	private final Map<Integer, TeamBuilder> teams = new HashMap<>();
	private final MatchBuilder builder;

	public TeamBuilderManager(final MatchBuilder builder) {
		this.builder = builder;
	}

	public void registerTeamsIntoGame(final Match game) {
		for (final TeamBuilder teamBuilder : teams.values()) {
			registerTeamIntoGame(teamBuilder, game);
		}
	}

	private void registerTeamIntoGame(TeamBuilder builder, Match match) {
		match.getGameDataStorage().getGameTeamManager().registerTeam(builder.build(match));
	}

	public TeamBuilder getTeam(UUID playerUniqueId) {
		for (TeamBuilder team : teams.values()) {
			for (Player member : team.getMembers()) {
				if (member.getUniqueId().equals(playerUniqueId)) {
					return team;
				}
			}
		}
		return null;
	}

	public MatchBuilder getBuilder() {
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
