package mc.obliviate.masterduels.api.arena.spectator;

import mc.obliviate.masterduels.api.user.IMember;
import org.bukkit.entity.Player;

import java.util.List;

public interface ISpectatorStorage {

	void unspectate(final Player player);

	void spectate(final Player player);

	boolean isSpectator(final Player player);

	List<Player> getSpectatorList();

	default void unspectateAll() {
		for (final Player player : getSpectatorList()) {
			unspectate(player);
		}
	}
}
