package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.team.Member;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

	private final BlokDuels plugin;

	public DamageListener(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPreDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player victim = (Player) e.getEntity();

			if (e.getFinalDamage() >= victim.getHealth()) {

				final Member member = DataHandler.getMember(victim.getUniqueId());
				if (member == null) return;
				if (member.getTeam() == null) Bukkit.broadcastMessage("team is null");
				if (member.getTeam().getGame() == null) Bukkit.broadcastMessage("game is null");
				e.setCancelled(true);

				member.getTeam().getGame().onDeath(member);

			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Player attacker = (Player) e.getDamager();
			plugin.getMessageAPI().sendActionBar(attacker, ChatColor.RED + "" + e.getDamage() + " ❤");
		}
	}
}