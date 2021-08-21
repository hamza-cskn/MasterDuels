package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.team.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PreDeathListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player victim = (Player) e.getEntity();

			if (e.getFinalDamage() >= victim.getHealth()) {

				final Member member = DataHandler.getMember(victim.getUniqueId());
				if (member == null) return;
				e.setCancelled(true);

				member.getTeam().getGame().onDeath(member);

			}
		}
	}
}
