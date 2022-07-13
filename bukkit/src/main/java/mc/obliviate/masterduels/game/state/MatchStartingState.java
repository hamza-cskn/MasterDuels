package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import org.bukkit.Bukkit;

public class MatchStartingState implements MatchState {

	private final Match match;

	public MatchStartingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelMatchStartEvent(match, this));
		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + match.getGameDataStorage().getMatchDuration().toMillis());

		for (Member member : match.getAllMembers()) {
			InventoryStorer.store(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "game-started");
		}

		//match time out task
		match.getGameTaskManager().delayedTask("time-out", () -> {
			match.uninstall();
			for (Member member : match.getAllMembers()) {
				MessageUtils.sendMessage(member.getPlayer(), "game-timed-out");
			}
		}, match.getGameDataStorage().getMatchDuration().toSeconds() * 20);
		match.getGameTaskManager().delayedTask("process-switcher-task", this::next, 0); //this is a strange bug fix.
	}

	@Override
	public void next() {
		if (!match.getMatchState().equals(this)) return;
		match.setGameState(new RoundStartingState(match));
	}

	@Override
	public void leave(Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		InventoryStorer.restore(member.getPlayer());
		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		Logger.debug(Logger.DebugPart.GAME, "Player " + member.getPlayer().getName() + " has left from duel game during duel game starting.");
		match.uninstall();
	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.MATCH_STARING;
	}

	@Override
	public Match getMatch() {
		return match;
	}
}
