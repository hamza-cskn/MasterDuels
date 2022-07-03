package mc.obliviate.masterduels.listeners;

import com.google.common.base.Preconditions;
import com.hakan.core.HCore;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.state.MatchState;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.entity.Entity;
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
			if (e instanceof EntityDamageByEntityEvent) {

				final Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
				if (damager instanceof Player) {
					final Member member = UserHandler.getMember(damager.getUniqueId());
					if (member == null) return;
					e.setCancelled(true);
				}

			}
			return;
		}

		final Player victim = (Player) e.getEntity();

		final IUser victimUser = UserHandler.getUser(victim.getUniqueId());
		if (victimUser == null) return;

		if (victimUser instanceof Member) {
			final Member victimMember = (Member) victimUser;

			Preconditions.checkNotNull(victimMember.getTeam(), victim.getPlayer() + " team cannot be null");
			Preconditions.checkNotNull(victimMember.getTeam().getMatch(), victim.getPlayer() + "match cannot be null");

			Member attackerMember = null;
			if (e instanceof EntityDamageByEntityEvent) {

				final Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
				if (damager instanceof Player) {

					attackerMember = UserHandler.getMember(damager.getUniqueId());
					if (attackerMember == null) {
						e.setCancelled(true);
						return;
					}

					if (attackerMember.getTeam().equals(victimMember.getTeam())) {
						e.setCancelled(true);
						//friendly fire protect
						return;
					}


					if (UserHandler.isSpectator(victim.getUniqueId()) || UserHandler.isSpectator(attackerMember.getPlayer().getUniqueId())) {
						e.setCancelled(true);
						//spectator protect
					}
				} else if (damager instanceof Projectile) {
					final Projectile projectile = (Projectile) damager;
					if (projectile.getShooter() instanceof Player) {
						attackerMember = UserHandler.getMember(((Player) projectile.getShooter()).getUniqueId());
					}
				}
			}

			MatchState gameState = victimMember.getTeam().getMatch().getMatchState();
			gameState.onDamage(e, victimMember, attackerMember);

		} else if (victim instanceof Spectator) {
			e.setCancelled(true);
		}


	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
				final Player attacker = (Player) ((Projectile) e.getDamager()).getShooter();
				final Member member = UserHandler.getMember(attacker.getUniqueId());
				if (member == null) return;
				final double distance = MessageUtils.getFirstDigits(e.getEntity().getLocation().toVector().distance(attacker.getLocation().toVector()), 2);
				MessageUtils.sendMessage(attacker, "arrow-hit-notify.message",
						new PlaceholderUtil()
								.add("{health}", ((Player) e.getEntity()).getHealthScale() + "")
								.add("{damage}", e.getFinalDamage() + "")
								.add("{victim}", e.getEntity().getName())
								.add("{distance}", distance + "")
				);
				HCore.sendActionBar(attacker,
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
