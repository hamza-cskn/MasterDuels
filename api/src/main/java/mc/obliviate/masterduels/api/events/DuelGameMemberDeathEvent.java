package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.api.user.IMember;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelGameMemberDeathEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final IMember victim;
	private final IMember attacker;

	public DuelGameMemberDeathEvent(IMember victim, IMember attacker) {
		this.victim = victim;
		this.attacker = attacker;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public IGame getGame() {
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
