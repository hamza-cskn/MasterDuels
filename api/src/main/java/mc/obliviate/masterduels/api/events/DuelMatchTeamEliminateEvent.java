package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.user.ITeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchTeamEliminateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ITeam team;
	private final DuelMatchMemberDeathEvent duelMatchMemberDeathEvent;

	public DuelMatchTeamEliminateEvent(final ITeam team, final DuelMatchMemberDeathEvent duelMatchMemberDeathEvent) {
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

	public IMatch getMatch() {
		return team.getMatch();
	}

	public ITeam getTeam() {
		return team;
	}

	public DuelMatchMemberDeathEvent getDuelGameMemberDeathEvent() {
		return duelMatchMemberDeathEvent;
	}
}
