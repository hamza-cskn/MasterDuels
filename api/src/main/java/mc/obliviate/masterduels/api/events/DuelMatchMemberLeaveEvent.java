package mc.obliviate.masterduels.api.events;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.user.IMember;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchMemberLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final IMember member;

	public DuelMatchMemberLeaveEvent(IMember member) {
		this.member = member;
	}

	public IMember getMember() {
		return member;
	}

	public IMatch getMatch() {
		return member.getMatch();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}