package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameMemberDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Member victim;
	private final Member attacker;

	public DuelGameMemberDeathEvent(Member victim, Member attacker) {
		this.victim = victim;
		this.attacker = attacker;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Game getGame() {
		return victim.getGame();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Member getVictim() {
		return victim;
	}

	/**
	 * @return null if member died without attacker
	 */
	public Member getAttacker() {
		return attacker;
	}
}
