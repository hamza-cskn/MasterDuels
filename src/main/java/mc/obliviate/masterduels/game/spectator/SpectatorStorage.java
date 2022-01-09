package mc.obliviate.masterduels.game.spectator;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.masterduels.api.events.spectator.DuelGamePreSpectatorJoinEvent;
import mc.obliviate.masterduels.api.events.spectator.DuelGameSpectatorLeaveEvent;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.User;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.user.team.Team;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import mc.obliviate.masterduels.utils.title.TitleHandler;
import org.bukkit.Bukkit;
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

	protected Spectator add(final Player player) {
		Spectator spectator = getSpectator(player);
		if (spectator != null) return spectator;
		spectator = new Spectator(game, player, !game.isMember(player));
		spectators.add(spectator);
		return spectator;
	}

	protected void remove(final Player player) {
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

		Bukkit.getPluginManager().callEvent(new DuelGameSpectatorLeaveEvent(spectator));

		spectators.remove(spectator);
		playerReset.reset(spectator.getPlayer());

		if (!game.isMember(spectator.getPlayer())) {
			DataHandler.getUsers().remove(spectator.getPlayer().getUniqueId());
			if (DataHandler.getLobbyLocation() != null) {
				spectator.getPlayer().teleport(DataHandler.getLobbyLocation());
			}
			InventoryStorer.restore(spectator.getPlayer());
			MessageAPI.getInstance(spectator.getGame().getPlugin()).sendTitle(spectator.getPlayer(), TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_LEAVE));

		}

	}

	public void spectate(final Player player) {
		final User user = DataHandler.getUser(player.getUniqueId());
		if (user == null || user instanceof Spectator || user instanceof Member) return;

		final DuelGamePreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelGamePreSpectatorJoinEvent(player, game);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;

		final Spectator spectator = add(player);

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		for (final Team team : game.getTeams().values()) {
			for (final Member m : team.getMembers()) {
				m.getPlayer().hidePlayer(player);
			}
		}

		for (final Player spec : getSpectators()) {
			spec.showPlayer(player);
			player.showPlayer(spec);
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		MessageUtils.sendMessage(player, "you-are-a-spectator");

		if (!game.isMember(player)) {
			player.teleport(spectator.getGame().getArena().getPositions().get("spawn-team-1").getLocation(1)); //todo make spectator location
			MessageAPI.getInstance(spectator.getGame().getPlugin()).sendTitle(spectator.getPlayer(), TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));

		}

	}


}
