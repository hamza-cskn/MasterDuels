package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.team.Member;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {


	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member != null) {
			member.getTeam().getGame().leave(member);
		}
	}

}
