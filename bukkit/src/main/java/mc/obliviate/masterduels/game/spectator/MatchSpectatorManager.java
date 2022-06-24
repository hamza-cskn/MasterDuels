package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.arena.spectator.IMatchSpectatorManager;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Match;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class MatchSpectatorManager implements IMatchSpectatorManager {

	private final SemiSpectatorStorage semiSpectatorStorage;
	private final PureSpectatorStorage pureSpectatorStorage;
	private final Match match;

	public MatchSpectatorManager(Match match) {
		this.semiSpectatorStorage = new SemiSpectatorStorage(this, match);
		this.pureSpectatorStorage = new PureSpectatorStorage(this, match);
		this.match = match;
	}

	public SemiSpectatorStorage getSemiSpectatorStorage() {
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
		if (match.getGameDataStorage().getGameTeamManager().getMember(player.getUniqueId()) != null) {
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
		if (match.getGameDataStorage().getGameTeamManager().isMember(player.getUniqueId())) {
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
