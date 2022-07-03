package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.user.Spectator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface SpectatorStorage {
	void unspectate(Spectator spectator);

	void unspectate(Player player);

	void spectate(Player player);

	boolean isSpectator(Player player);

	default void unspectateAll() {
		for (final Spectator spectator : new ArrayList<>(getSpectatorList())) {
			unspectate(spectator);
		}
	}

	List<Spectator> getSpectatorList();
}
