package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.DuelUser;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

	private final MasterDuels plugin;

	public PlayerConnectionListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		final Member member = DataHandler.getMember(event.getPlayer().getUniqueId());
		if (member != null) {
			member.getTeam().getMatch().getMatchState().leave(member);
		}
	}

	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		DuelUser.loadDuelUser(plugin.getSqlManager(), event.getPlayer());
	}

}
