package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.user.spectator.Spectator;
import org.bukkit.entity.Player;

import java.util.List;

public interface SpectatorStorage {
	void unspectate(Spectator spectator);

	void unspectate(Player player);

	void spectate(Player player);

	boolean isSpectator(Player player);

	default void unspectateAll() {
		for (final Spectator spectator : getSpectatorList()) {
			unspectate(spectator);
		}
	}

	List<Spectator> getSpectatorList();
}
