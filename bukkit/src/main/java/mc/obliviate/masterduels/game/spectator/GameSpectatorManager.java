package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.arena.spectator.IGameSpectatorManager;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class GameSpectatorManager implements IGameSpectatorManager {

	private final OmniSpectatorStorage omniSpectatorStorage;
	private final PureSpectatorStorage pureSpectatorStorage;
	private final Game game;

	public GameSpectatorManager(Game game) {
		this.omniSpectatorStorage = new OmniSpectatorStorage(this, game);
		this.pureSpectatorStorage = new PureSpectatorStorage(this, game);
		this.game = game;
	}

	public OmniSpectatorStorage getOmniSpectatorStorage() {
		return omniSpectatorStorage;
	}

	public PureSpectatorStorage getPureSpectatorStorage() {
		return pureSpectatorStorage;
	}

	public void spectate(IMember member) {
		omniSpectatorStorage.spectate(member.getPlayer());
	}

	public void spectate(ISpectator spectator) {
		pureSpectatorStorage.spectate(spectator.getPlayer());
	}

	public void spectate(Player player) {
		if (game.getGameDataStorage().getGameTeamManager().getMember(player.getUniqueId()) != null) {
			omniSpectatorStorage.spectate(player);
			return;
		}
		pureSpectatorStorage.spectate(player);
	}

	public void unspectate(IMember member) {
		omniSpectatorStorage.unspectate(member.getPlayer());
	}

	public void unspectate(ISpectator spectator) {
		pureSpectatorStorage.unspectate(spectator.getPlayer());
	}

	public void unspectate(Player player) {
		if (game.getGameDataStorage().getGameTeamManager().getMember(player.getUniqueId()) != null) { //todo change check usage
			omniSpectatorStorage.unspectate(player);
			return;
		}
		pureSpectatorStorage.unspectate(player);
	}

	public List<ISpectator> getAllSpectators() {
		final List<ISpectator> spectators = new ArrayList<>(omniSpectatorStorage.getSpectatorList());
		spectators.addAll(pureSpectatorStorage.getSpectatorList());
		return spectators;
	}

	public boolean isSpectator(Player player) {
		return omniSpectatorStorage.isSpectator(player) || pureSpectatorStorage.isSpectator(player);
	}
}
