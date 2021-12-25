package mc.obliviate.blokduels.api.events;

import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.user.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameTeamEleminateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Team team;
	private final DuelGameMemberDeathEvent duelGameMemberDeathEvent;

	public DuelGameTeamEleminateEvent(final Team team, final DuelGameMemberDeathEvent duelGameMemberDeathEvent) {
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

	public Game getGame() {
		return team.getGame();
	}

	public Team getTeam() {
		return team;
	}

	public DuelGameMemberDeathEvent getDuelGameMemberDeathEvent() {
		return duelGameMemberDeathEvent;
	}
}
