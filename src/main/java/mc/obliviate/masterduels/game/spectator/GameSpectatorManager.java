package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.spectator.ISpectator;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class GameSpectatorManager {

	private final OmniSpectatorStorage omniSpectatorStorage;
	private final PureSpectatorStorage pureSpectatorStorage;
	private final Game game;

	public GameSpectatorManager(Game game) {
		this.omniSpectatorStorage = new OmniSpectatorStorage(this,game);
		this.pureSpectatorStorage = new PureSpectatorStorage(this, game);
		this.game = game;
	}

	public OmniSpectatorStorage getOmniSpectatorStorage() {
		return omniSpectatorStorage;
	}

	public PureSpectatorStorage getPureSpectatorStorage() {
		return pureSpectatorStorage;
	}

	public void spectate(Member member) {
		omniSpectatorStorage.spectate(member.getPlayer());
	}
	public void spectate(ISpectator spectator) {
		pureSpectatorStorage.spectate(spectator.getPlayer());
	}
	public void spectate(Player player) {
		if (game.isMember(player)) {
			omniSpectatorStorage.spectate(player);
			return;
		}
		pureSpectatorStorage.spectate(player);
	}

	public void unspectate(Member member) {
		omniSpectatorStorage.unspectate(member.getPlayer());
	}
	public void unspectate(ISpectator spectator) {
		pureSpectatorStorage.unspectate(spectator.getPlayer());
	}
	public void unspectate(Player player) {
		if (game.isMember(player)) {
			omniSpectatorStorage.unspectate(player);
			return;
		}
		pureSpectatorStorage.unspectate(player);
	}

	public List<Player> getAllSpectators() {
		final List<Player> spectators = new ArrayList<>(omniSpectatorStorage.getSpectatorList());
		spectators.addAll(pureSpectatorStorage.getSpectatorList());
		return spectators;
	}

	public boolean isSpectator(Player player) {
		return omniSpectatorStorage.isSpectator(player) || pureSpectatorStorage.isSpectator(player);
	}
}
