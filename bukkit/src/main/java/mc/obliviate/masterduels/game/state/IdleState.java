package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchPreStartEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IdleState implements MatchState {

	private final Match match;

	public IdleState(Match match) {
		this.match = match;
	}

	@Override
	public void next() {
		Bukkit.getPluginManager().callEvent(new DuelMatchPreStartEvent(match));
		match.setGameState(new MatchStartingState(match));
	}

	@Override
	public void leave(Member member) {
		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		match.removeMember(member);
	}

	@Override
	public void rejoin(Player player, Kit kit, int teamNo) {
		match.addPlayer(player, kit, teamNo);
	}

	@Override
	public Match getMatch() {
		return match;
	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.IDLE;
	}
}
