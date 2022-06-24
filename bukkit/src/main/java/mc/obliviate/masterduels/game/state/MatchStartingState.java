package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.events.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.events.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;

public class MatchStartingState implements MatchState {

	private final Match match;

	public MatchStartingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelMatchStartEvent(match));
		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + match.getGameDataStorage().getMatchDuration().toMillis());

		//match time out task
		match.getGameTaskManager().delayedTask("time-out", match::uninstall, match.getGameDataStorage().getMatchDuration().toSeconds() * 20);
		match.getGameTaskManager().delayedTask("process-switcher-task", this::next, 0); //this is a strange bug fix.
	}

	@Override
	public void next() {
		match.setGameState(new RoundStartingState(match));
	}

	@Override
	public void leave(IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		Logger.debug(Logger.DebugPart.GAME, "Player " + member.getPlayer().getName() + " has left from duel game during duel game starting.");
		match.uninstall();
	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.GAME_STARING;
	}

	@Override
	public Match getMatch() {
		return null;
	}
}
