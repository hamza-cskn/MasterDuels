package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

public class IdleState implements GameState {

	private final Game match;

	public IdleState(Game match) {
		this.match = match;
	}

	@Override
	public void next() {
		match.setGameState(new GameStartingState(match));
	}

	@Override
	public void leave(IMember member) {
		match.removePlayer(member.getPlayer().getUniqueId());
	}

	@Override
	public void join(Player player, IKit kit, int teamNo) {
		match.addPlayer(player, kit, teamNo);
	}

	@Override
	public Game getMatch() {
		return match;
	}

	@Override
	public GameStateType getGameStateType() {
		return GameStateType.IDLE;
	}
}
