package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.team.Member;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {


	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member != null) {
			member.getTeam().getGame().leaveMember(member);
		}
	}

}
