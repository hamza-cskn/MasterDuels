package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.arena.spectator.IGameSpectatorManager;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class GameSpectatorManager implements IGameSpectatorManager {

	private final SemiSpectatorStorage semiSpectatorStorage;
	private final PureSpectatorStorage pureSpectatorStorage;
	private final Game game;

	public GameSpectatorManager(Game game) {
		this.semiSpectatorStorage = new SemiSpectatorStorage(this, game);
		this.pureSpectatorStorage = new PureSpectatorStorage(this, game);
		this.game = game;
	}

	public SemiSpectatorStorage getOmniSpectatorStorage() {
		return semiSpectatorStorage;
	}

	public PureSpectatorStorage getPureSpectatorStorage() {
		return pureSpectatorStorage;
	}

	public void spectate(IMember member) {
		semiSpectatorStorage.spectate(member.getPlayer());
	}

	public void spectate(ISpectator spectator) {
		pureSpectatorStorage.spectate(spectator.getPlayer());
	}

	public void spectate(Player player) {
		if (game.getGameDataStorage().getGameTeamManager().getMember(player.getUniqueId()) != null) {
			semiSpectatorStorage.spectate(player);
			return;
		}
		pureSpectatorStorage.spectate(player);
	}

	public void unspectate(IMember member) {
		semiSpectatorStorage.unspectate(member.getPlayer());
	}

	public void unspectate(ISpectator spectator) {
		pureSpectatorStorage.unspectate(spectator.getPlayer());
	}

	public void unspectate(Player player) {
		if (game.getGameDataStorage().getGameTeamManager().isMember(player.getUniqueId())) {
			semiSpectatorStorage.unspectate(player);
			return;
		}
		pureSpectatorStorage.unspectate(player);
	}

	public List<ISpectator> getAllSpectators() {
		final List<ISpectator> spectators = new ArrayList<>(semiSpectatorStorage.getSpectatorList());
		spectators.addAll(pureSpectatorStorage.getSpectatorList());
		return spectators;
	}

	public boolean isSpectator(Player player) {
		return semiSpectatorStorage.isSpectator(player) || pureSpectatorStorage.isSpectator(player);
	}
}
