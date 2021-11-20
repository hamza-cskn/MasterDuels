package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.spectator.SpectatorData;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
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

			final Member member = DataHandler.getMember(victim.getUniqueId());
			if (member == null) return;
			if (member.getTeam() == null) Bukkit.broadcastMessage("team is null");
			if (member.getTeam().getGame() == null) Bukkit.broadcastMessage("game is null");


			if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				final Member attackerMember = DataHandler.getMember(((EntityDamageByEntityEvent) e).getDamager().getUniqueId());
				if (attackerMember == null) {
					e.setCancelled(true);
					return;
				}
				if (attackerMember.getTeam().equals(member.getTeam())) {
					e.setCancelled(true);
					attackerMember.getPlayer().sendMessage("Takımındaki adama vuramazsın ke.");
					return;
				}
				//todo spectators can hit non-player entities.
				final SpectatorData spectatorData = member.getTeam().getGame().getSpectatorData();
				if (spectatorData.isSpectator(victim) || spectatorData.isSpectator(attackerMember.getPlayer())) {
					e.setCancelled(true);
					member.getPlayer().sendMessage("Izleyiciyken birine vuramazsın veya izleyicilere vuramazsın ke.");
				}
			}

			if (e.getFinalDamage() >= victim.getHealth()) {
				e.setCancelled(true);
				member.getTeam().getGame().onDeath(member);

			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			final Member member = DataHandler.getMember(e.getDamager().getUniqueId());
			if (member == null) return;
			final Player attacker = (Player) e.getDamager();
			plugin.getMessageAPI().sendActionBar(attacker, ChatColor.RED + "" + e.getDamage() + " ❤");
		}
	}
}
