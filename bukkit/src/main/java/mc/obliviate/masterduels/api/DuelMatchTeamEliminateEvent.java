package mc.obliviate.masterduels.api;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchTeamEliminateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Team team;
	private final DuelMatchMemberDeathEvent duelMatchMemberDeathEvent;

	public DuelMatchTeamEliminateEvent(final Team team, final DuelMatchMemberDeathEvent duelMatchMemberDeathEvent) {
		this.team = team;
		this.duelMatchMemberDeathEvent = duelMatchMemberDeathEvent;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Match getMatch() {
		return team.getMatch();
	}

	public Team getTeam() {
		return team;
	}

	public DuelMatchMemberDeathEvent getDuelGameMemberDeathEvent() {
		return duelMatchMemberDeathEvent;
	}
}
