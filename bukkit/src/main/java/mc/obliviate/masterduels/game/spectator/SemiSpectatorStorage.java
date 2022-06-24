package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.arena.spectator.ISpectatorStorage;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SemiSpectatorStorage implements ISpectatorStorage {

	protected static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	private final GameSpectatorManager gsm;
	private final List<ISpectator> spectators = new ArrayList<>();
	private final Game game;

	public SemiSpectatorStorage(GameSpectatorManager gsm, Game game) {
		this.gsm = gsm;
		this.game = game;
	}

	private ISpectator findSpectator(Player player) {
		for (final ISpectator spectator : spectators) {
			if (spectator.getPlayer().equals(player)) {
				return spectator;
			}
		}
		return null;
	}

	@Override
	public void unspectate(ISpectator spectator) {
		if (!spectators.remove(spectator)) return;

		//Bukkit.getPluginManager().callEvent(new DuelGameSpectatorLeaveEvent(spectator));
		Bukkit.broadcastMessage("omni spectator unspectate()");
		playerReset.reset(spectator.getPlayer());

		for (final IMember member : game.getAllMembers()) {
			member.getPlayer().showPlayer(spectator.getPlayer());
		}
	}

	@Override
	public void unspectate(Player player) {
		final ISpectator spectator = findSpectator(player);
		if (spectator == null) return;
		unspectate(spectator);

	}

	@Override
	public void spectate(Player player) {
		final ISpectator spectator = findSpectator(player);
		if (spectator != null) return;

		spectators.add(new Spectator(null, player));

		/*final DuelGamePreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelGamePreSpectatorJoinEvent(player, game);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;
		 */

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		Bukkit.broadcastMessage("omni spectator spectate()");

		for (final IMember member : game.getAllMembers()) {
			member.getPlayer().hidePlayer(player);
		}

		for (final ISpectator spec : gsm.getAllSpectators()) {
			spec.getPlayer().showPlayer(player);
			player.showPlayer(spec.getPlayer());
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		MessageUtils.sendMessage(player, "you-are-a-spectator");

		//MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));
	}

	@Override
	public boolean isSpectator(Player player) {
		return spectators.contains(player);
	}

	@Override
	public List<ISpectator> getSpectatorList() {
		return spectators;
	}
}
