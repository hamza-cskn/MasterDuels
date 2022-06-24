package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;

public class GameStartingState implements GameState {

	private final Game match;

	public GameStartingState(Game match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelGameStartEvent(match));
		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + match.getGameDataStorage().getMatchDuration().toMillis());

		//match time out task
		match.getGameTaskManager().delayedTask("time-out", match::uninstall, match.getGameDataStorage().getMatchDuration().toSeconds() * 20);
		next();
	}

	@Override
	public void next() {
		match.setGameState(new RoundStartingState(match));
	}

	@Override
	public void leave(IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Logger.debug(Logger.DebugPart.GAME, "Player " + member.getPlayer().getName() + " has left from duel game during duel game starting.");
		match.uninstall();
	}

	@Override
	public GameStateType getGameStateType() {
		return GameStateType.GAME_STARING;
	}

	@Override
	public Game getMatch() {
		return null;
	}
}
