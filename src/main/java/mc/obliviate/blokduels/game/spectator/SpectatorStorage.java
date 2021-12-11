package mc.obliviate.blokduels.game.spectator;

import com.hakan.messageapi.bukkit.MessageAPI;
import com.hakan.messageapi.bukkit.title.Title;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.kit.InventoryStorer;
import mc.obliviate.blokduels.kit.PlayerInventoryFrame;
import mc.obliviate.blokduels.user.Spectator;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.playerreset.PlayerReset;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectatorStorage {

	private static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	private final List<Spectator> spectators = new ArrayList<>();
	private final Game game;

	public SpectatorStorage(Game game) {
		this.game = game;
	}

	public List<Player> getSpectators() {
		final List<Player> players = new ArrayList<>();
		spectators.forEach(spectator -> players.add(spectator.getPlayer()));
		return players;
	}

	public Spectator getSpectator(Player player) {
		for (final Spectator spectator : spectators) {
			if (spectator.getPlayer().equals(player)) {
				return spectator;
			}
		}
		return null;
	}

	public Spectator add(final Player player) {
		Spectator spectator = getSpectator(player);
		if (spectator != null) return spectator;
		spectator = new Spectator(game, player, !game.isMember(player));
		spectators.add(spectator);
		return spectator;
	}

	public void remove(final Player player) {
		spectators.removeIf(spectator -> spectator.getPlayer().equals(player));
	}

	public boolean isSpectator(Player p) {
		return getSpectator(p) != null;
	}

	public boolean isSpectator(Spectator spectator) {
		return spectators.contains(spectator);
	}

	public void unSpectateMembers() {
		for (final Member member : game.getAllMembers()) {
			if (isSpectator(member.getPlayer())) {
				unspectate(member.getPlayer());
			}
		}
	}

	public void unspectate(final Player player) {
		unspectate(getSpectator(player));
	}

	public void unspectate(final Spectator spectator) {
		if (!isSpectator(spectator)) return;

		spectators.remove(spectator);
		playerReset.reset(spectator.getPlayer());

		if (!game.isMember(spectator.getPlayer())) {
			DataHandler.getUsers().remove(spectator.getPlayer().getUniqueId());
			spectator.getPlayer().teleport(DataHandler.getLobbyLocation());
			InventoryStorer.restore(spectator.getPlayer());
			MessageAPI.getInstance(spectator.getGame().getPlugin()).sendTitle(spectator.getPlayer(), new Title("", MessageUtils.parseColor("&cIzleyici modundan ayrıldınız!"), 20, 5, 5));

		}

	}

	public void spectate(final Player player) {
		if (isSpectator(player)) return;
		final Spectator spectator = add(player);

		if (!game.isMember(player)) {
			player.teleport(spectator.getGame().getArena().getPositions().get("spawn-team-1").getLocation(1)); //todo make spectator location
			MessageAPI.getInstance(spectator.getGame().getPlugin()).sendTitle(player, new Title("", MessageUtils.parseColor("&7Izleyici moduna geçtiniz!"), 20, 5, 5));

		}

	}


}
