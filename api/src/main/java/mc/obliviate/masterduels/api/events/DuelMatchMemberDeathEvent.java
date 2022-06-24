package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.user.IMember;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class DuelMatchMemberDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final EntityDamageEvent entityDamageEvent;
	private final IMember victim;
	private final IMember attacker;

	public DuelMatchMemberDeathEvent(EntityDamageEvent entityDamageEvent, IMember victim, IMember attacker) {
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

	public IMatch getMatch() {
		return victim.getGame();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public IMember getVictim() {
		return victim;
	}

	/**
	 * @return null if member died without attacker
	 */
	public IMember getAttacker() {
		return attacker;
	}
}
