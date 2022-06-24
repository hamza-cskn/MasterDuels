package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.data.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {


	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		final IMember member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member != null) {
			member.getTeam().getGame().getGameState().leave(member);
		}
	}

}
