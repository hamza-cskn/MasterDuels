package mc.obliviate.blokduels.game.spectator;

import com.hakan.messageapi.bukkit.MessageAPI;
import com.hakan.messageapi.bukkit.title.Title;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.playerreset.PlayerReset;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectatorStorage {

	private static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	private final List<Player> spectators = new ArrayList<>();
	private final Game game;

	public SpectatorStorage(Game game) {
		this.game = game;
	}

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

	public void unSpectateMembers() {
		for (final Member member : game.getAllMembers()) {
			unspectate(member.getPlayer());
		}
	}

	public void unspectate(final Player player) {
		if (isSpectator(player)) return;

		remove(player);
		playerReset.reset(player);

	}

	public void spectate(final Player player) {
		if (isSpectator(player)) return;

		if (player.teleport(game.getArena().getPositions().get("spawn-team-1").getLocation(1))) { //todo make spectator location
			add(player);
			MessageAPI.getInstance(game.getPlugin()).sendTitle(player, new Title("", MessageUtils.parseColor("&7Izleyici moduna geçtiniz!"), 20, 5, 5));

		}

	}


}
