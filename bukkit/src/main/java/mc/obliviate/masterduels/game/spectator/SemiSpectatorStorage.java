package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SemiSpectatorStorage implements SpectatorStorage {

	protected static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	private final MatchSpectatorManager gsm;
	private final List<Spectator> spectators = new ArrayList<>();
	private final Match game;

	public SemiSpectatorStorage(MatchSpectatorManager gsm, Match game) {
		this.gsm = gsm;
		this.game = game;
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

		//Bukkit.getPluginManager().callEvent(new DuelGameSpectatorLeaveEvent(spectator));
		Bukkit.broadcastMessage("omni spectator unspectate()");
		playerReset.reset(spectator.getPlayer());

		for (final Member member : game.getAllMembers()) {
			member.getPlayer().showPlayer(spectator.getPlayer());
		}
	}

	@Override
	public void unspectate(Player player) {
		final Spectator spectator = findSpectator(player);
		if (spectator == null) return;
		unspectate(spectator);

	}

	@Override
	public void spectate(Player player) {
		final Spectator spectator = findSpectator(player);
		if (spectator != null) return;

		spectators.add(new Spectator(null, player));

		/*final DuelGamePreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelGamePreSpectatorJoinEvent(player, game);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;
		 */

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		Bukkit.broadcastMessage("omni spectator spectate()");

		for (final Member member : game.getAllMembers()) {
			member.getPlayer().hidePlayer(player);
		}

		for (final Spectator spec : gsm.getAllSpectators()) {
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
	public List<Spectator> getSpectatorList() {
		return spectators;
	}
}
