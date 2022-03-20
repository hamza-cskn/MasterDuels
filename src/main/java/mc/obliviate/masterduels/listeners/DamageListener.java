package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.spectator.GameSpectatorManager;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

	private final MasterDuels plugin;

	public DamageListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPreDeath(EntityDamageEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) {
			if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				final Member member = DataHandler.getMember((((EntityDamageByEntityEvent) e).getDamager()).getUniqueId());
				if (member == null) return;
				e.setCancelled(true);
			}
			return;
		}

		final Player victim = (Player) e.getEntity();

		final IUser victimUser = DataHandler.getUser(victim.getUniqueId());
		if (victimUser == null) return;

		if (victimUser instanceof Member) {
			final Member victimMember = (Member) victimUser;
			Member attackerMember = null;

			if (victimMember.getTeam() == null) Logger.error("Team is null! on damage listener");
			if (victimMember.getTeam().getGame() == null) Logger.error("Game is null! on damage listener");


			if (e instanceof EntityDamageByEntityEvent) {
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
					attackerMember = DataHandler.getMember(((EntityDamageByEntityEvent) e).getDamager().getUniqueId());
					if (attackerMember == null) {
						e.setCancelled(true);
						return;
					}
					if (attackerMember.getTeam().equals(victimMember.getTeam())) {
						e.setCancelled(true);
						//friendly protect
						return;
					}
					final GameSpectatorManager spectatorData = victimMember.getTeam().getGame().getSpectatorManager();
					if (spectatorData.isSpectator(victim) || spectatorData.isSpectator(attackerMember.getPlayer())) {
						e.setCancelled(true);
						//spectator protect
					}
				} else if (((EntityDamageByEntityEvent) e).getDamager() instanceof Projectile) {
					final Projectile projectile = (Projectile) ((EntityDamageByEntityEvent) e).getDamager();
					if (projectile.getShooter() instanceof Player) {
						attackerMember = DataHandler.getMember(((Player) projectile.getShooter()).getUniqueId());
					}
				}
			}
			if (e.getFinalDamage() >= victim.getHealth()) {
				e.setCancelled(true);
				victimMember.getTeam().getGame().onDeath(victimMember, attackerMember);
			}
		}

		else if (victim instanceof Spectator) {
			e.setCancelled(true);
		}


	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
				final Player attacker = (Player) ((Projectile) e.getDamager()).getShooter();
				final Member member = DataHandler.getMember(attacker.getUniqueId());
				if (member == null) return;
				final double distance = MessageUtils.getFirstDigits(e.getEntity().getLocation().toVector().distance(attacker.getLocation().toVector()), 2);
				MessageUtils.sendMessage(attacker, "arrow-hit-notify.message",
						new PlaceholderUtil()
								.add("{health}", ((Player) e.getEntity()).getHealthScale() + "")
								.add("{damage}", e.getFinalDamage() + "")
								.add("{victim}", e.getEntity().getName())
								.add("{distance}", distance + "")
				);
				plugin.getMessageAPI().sendActionBar(attacker,
						MessageUtils.parseColor(
								MessageUtils.applyPlaceholders(
										MessageUtils.getMessage("arrow-hit-notify.action-bar"),
										new PlaceholderUtil()
												.add("{health}", ((Player) e.getEntity()).getHealthScale() + "")
												.add("{damage}", e.getFinalDamage() + "")
												.add("{victim}", e.getEntity().getName())
												.add("{distance}", distance + "")
								)));
			}
		}
	}
}
