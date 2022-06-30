package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.spectator.DuelMatchPreSpectatorJoinEvent;
import mc.obliviate.masterduels.api.spectator.DuelMatchSpectatorLeaveEvent;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static mc.obliviate.masterduels.game.spectator.SemiSpectatorStorage.playerReset;

/**
 * Purpose of this class
 * storing PURE SPECTATOR players.
 * <p>
 * PURE SPECTATORS
 * Spectator players from out of game,
 * not member.
 */
public class PureSpectatorStorage implements SpectatorStorage {

	private final MatchSpectatorManager gsm;
	private final List<Spectator> spectators = new ArrayList<>();
	private final Match match;

	public PureSpectatorStorage(MatchSpectatorManager gsm) {
		this.gsm = gsm;
		this.match = gsm.getMatch();
	}

	private Spectator findSpectator(Player player) {
		for (final Spectator spectator : spectators) {
			if (spectator.getPlayer().equals(player)) {
				return spectator;
			}
		}
		return null;
	}

	@Override
	public void unspectate(Spectator spectator) {
		if (!spectators.remove(spectator)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchSpectatorLeaveEvent(spectator));
		playerReset.reset(spectator.getPlayer());

		UserHandler.switchUser(spectator);
		if (DataHandler.getLobbyLocation() != null) {
			spectator.getPlayer().teleport(DataHandler.getLobbyLocation());
		}
		InventoryStorer.restore(spectator.getPlayer());
		//MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_LEAVE));

	}

	@Override
	public void unspectate(Player player) {
		final Spectator spectator = findSpectator(player);
		if (spectator == null) return;
		unspectate(spectator);
	}

	@Override
	public void spectate(Player player) {
		Spectator spectator = findSpectator(player);
		if (spectator != null) return;

		final DuelMatchPreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelMatchPreSpectatorJoinEvent(player, match);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;

		spectator = UserHandler.switchSpectator(UserHandler.getUser(player.getUniqueId()), match);

		//fixme external registering
		SpectatorHandler.giveSpectatorItems(player);

		spectators.add(spectator);

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		for (final Team team : match.getGameDataStorage().getGameTeamManager().getTeams()) {
			for (final Member m : team.getMembers()) {
				m.getPlayer().hidePlayer(player);
			}
		}

		for (final Spectator spec : gsm.getAllSpectators()) {
			spec.getPlayer().showPlayer(player);
			player.showPlayer(spec.getPlayer());
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		MessageUtils.sendMessage(player, "you-are-a-spectator");

		player.teleport(match.getArena().getSpectatorLocation());
		//MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));


	}

	@Override
	public boolean isSpectator(Player player) {
		return findSpectator(player) != null;
	}


	@Override
	public List<Spectator> getSpectatorList() {
		return spectators;
	}
}
