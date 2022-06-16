package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.user.ITeam;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameTeamEliminateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ITeam team;
	private final DuelGameMemberDeathEvent duelGameMemberDeathEvent;

	public DuelGameTeamEliminateEvent(final ITeam team, final DuelGameMemberDeathEvent duelGameMemberDeathEvent) {
		this.team = team;
		this.duelGameMemberDeathEvent = duelGameMemberDeathEvent;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IGame getGame() {
		return team.getGame();
	}

	public ITeam getTeam() {
		return team;
	}

	public DuelGameMemberDeathEvent getDuelGameMemberDeathEvent() {
		return duelGameMemberDeathEvent;
	}
}
