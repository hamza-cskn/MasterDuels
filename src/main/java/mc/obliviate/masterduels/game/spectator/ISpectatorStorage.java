package mc.obliviate.masterduels.game.spectator;

import org.bukkit.entity.Player;

import java.util.List;

public interface ISpectatorStorage {

	void unspectate(final Player player);
	void spectate(final Player player);
	boolean isSpectator(final Player player);
	List<Player> getSpectatorList();
}
