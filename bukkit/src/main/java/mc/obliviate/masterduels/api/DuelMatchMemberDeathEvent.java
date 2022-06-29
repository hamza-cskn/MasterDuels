package mc.obliviate.masterduels.api;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class DuelMatchMemberDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final EntityDamageEvent entityDamageEvent;
	private final Member victim;
	private final Member attacker;

	public DuelMatchMemberDeathEvent(EntityDamageEvent entityDamageEvent, Member victim, Member attacker) {
		this.entityDamageEvent = entityDamageEvent;
		this.victim = victim;
		this.attacker = attacker;
	}

	public EntityDamageEvent getEntityDamageEvent() {
		return entityDamageEvent;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Match getMatch() {
		return victim.getMatch();
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
