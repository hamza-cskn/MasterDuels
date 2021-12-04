package mc.obliviate.blokduels.bukkit.game.spectator;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectatorData {

	private final List<Player> spectators = new ArrayList<>();

	public List<Player> getSpectators() {
		return spectators;
	}

	public void add(final Player player) {
		spectators.add(player);
	}

	public void remove(final Player player) {
		spectators.remove(player);
	}

	public boolean isSpectator(Player p) {
		return spectators.contains(p);
	}
}
