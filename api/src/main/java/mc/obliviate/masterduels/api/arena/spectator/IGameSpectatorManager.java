package mc.obliviate.masterduels.api.arena.spectator;

import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import org.bukkit.entity.Player;

import java.util.List;

public interface IGameSpectatorManager {

	ISpectatorStorage getOmniSpectatorStorage();

	ISpectatorStorage getPureSpectatorStorage();

	void spectate(IMember member);

	void spectate(ISpectator spectator);

	void spectate(Player player);

	void unspectate(IMember member);

	void unspectate(ISpectator spectator);

	void unspectate(Player player);

	List<ISpectator> getAllSpectators();

	boolean isSpectator(Player player);

}
