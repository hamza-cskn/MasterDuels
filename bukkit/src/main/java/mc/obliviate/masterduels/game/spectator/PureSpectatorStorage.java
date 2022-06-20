package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.arena.spectator.ISpectatorStorage;
import mc.obliviate.masterduels.api.events.spectator.DuelGamePreSpectatorJoinEvent;
import mc.obliviate.masterduels.api.events.spectator.DuelGameSpectatorLeaveEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static mc.obliviate.masterduels.game.spectator.OmniSpectatorStorage.playerReset;

/**
 * Purpose of this class
 * storing PURE SPECTATOR players.
 * <p>
 * PURE SPECTATORS
 * Spectator players from out of game,
 * not member.
 */
public class PureSpectatorStorage implements ISpectatorStorage {

	private final GameSpectatorManager gsm;
	private final List<Player> spectators = new ArrayList<>();
	private final Game game;

	public PureSpectatorStorage(GameSpectatorManager gsm, Game game) {
		this.gsm = gsm;
		this.game = game;
	}


	@Override
	public void unspectate(Player player) {
		if (!spectators.remove(player)) return;

		final ISpectator spectator = DataHandler.getSpectator(player.getUniqueId());
		if (spectator == null) return;

		Bukkit.getPluginManager().callEvent(new DuelGameSpectatorLeaveEvent(spectator));
		Bukkit.broadcastMessage("pure spectator unspectate()");
		playerReset.reset(player);

		DataHandler.getUsers().remove(player.getUniqueId());
		if (DataHandler.getLobbyLocation() != null) {
			player.teleport(DataHandler.getLobbyLocation());
		}
		InventoryStorer.restore(player);
		//MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_LEAVE));


	}

	@Override
	public void spectate(Player player) {
		if (spectators.contains(player)) return;
		spectators.add(player);

		final DuelGamePreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelGamePreSpectatorJoinEvent(player, game);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;

		DataHandler.getUsers().put(player.getUniqueId(), new Spectator(game, player));

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		Bukkit.broadcastMessage("pure spectator spectate()");

		for (final ITeam team : game.getTeams().values()) {
			for (final IMember m : team.getMembers()) {
				m.getPlayer().hidePlayer(player);
			}
		}


		for (final Player spec : gsm.getAllSpectators()) {
			spec.showPlayer(player);
			player.showPlayer(spec);
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		MessageUtils.sendMessage(player, "you-are-a-spectator");

		player.teleport(game.getArena().getSpectatorLocation());
		//MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));


	}

	@Override
	public boolean isSpectator(Player player) {
		return spectators.contains(player);
	}


	@Override
	public List<Player> getSpectatorList() {
		return spectators;
	}
}
