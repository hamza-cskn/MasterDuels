package mc.obliviate.masterduels.api.arena.spectator;

import mc.obliviate.masterduels.api.user.ISpectator;
import org.bukkit.entity.Player;

import java.util.List;

public interface ISpectatorStorage {

	void unspectate(final ISpectator spectator);

	void unspectate(final Player player);

	void spectate(final Player player);

	boolean isSpectator(final Player player);

	List<ISpectator> getSpectatorList();

	default void unspectateAll() {
		for (final ISpectator spectator : getSpectatorList()) {
			unspectate(spectator);
		}
	}
}
