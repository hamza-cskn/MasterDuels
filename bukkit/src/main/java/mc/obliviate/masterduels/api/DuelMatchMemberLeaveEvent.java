package mc.obliviate.masterduels.api;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.Member;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchMemberLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Member member;

	public DuelMatchMemberLeaveEvent(Member member) {
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	public Match getMatch() {
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