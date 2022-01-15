package mc.obliviate.masterduels.game.spectator;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import mc.obliviate.masterduels.utils.title.TitleHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OmniSpectatorStorage implements ISpectatorStorage {

	protected static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
	private final GameSpectatorManager gsm;
	private final List<Player> spectators = new ArrayList<>();
	private final Game game;

	public OmniSpectatorStorage(GameSpectatorManager gsm, Game game) {
		this.gsm = gsm;
		this.game = game;
	}

	public void unSpectateMembers() {
		for (final Member member : game.getAllMembers()) {
			if (spectators.contains(member.getPlayer())) {
				unspectate(member.getPlayer());
			}
		}
	}

	@Override
	public void unspectate(Player player) {
		if (!spectators.remove(player)) return;

		//Bukkit.getPluginManager().callEvent(new DuelGameSpectatorLeaveEvent(spectator));
		Bukkit.broadcastMessage("omni spectator unspectate()");
		playerReset.reset(player);

		for (final Member member : game.getAllMembers()) {
			member.getPlayer().showPlayer(player);
		}

		MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_LEAVE));

	}

	@Override
	public void spectate(Player player) {
		if (spectators.contains(player)) return;
		spectators.add(player);

		/*final DuelGamePreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelGamePreSpectatorJoinEvent(player, game);
		Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
		if (duelGamePreSpectatorJoinEvent.isCancelled()) return;
		 */

		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);

		Bukkit.broadcastMessage("omni spectator spectate()");

		for (final Member m : game.getAllMembers()) {
			m.getPlayer().hidePlayer(player);
		}

		for (final Player spec : gsm.getAllSpectators()) {
			spec.showPlayer(player);
			player.showPlayer(spec);
		}

		player.setAllowFlight(true);
		player.setFlying(true);

		MessageUtils.sendMessage(player, "you-are-a-spectator");

		MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));
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
