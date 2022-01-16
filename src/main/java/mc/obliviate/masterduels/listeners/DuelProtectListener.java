package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.spectator.ISpectator;
import mc.obliviate.masterduels.user.team.IMember;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

public class DuelProtectListener implements Listener {

	private final MasterDuels plugin;
	private final boolean teleportBackWhenLimitViolate;

	public DuelProtectListener(MasterDuels plugin) {
		this.plugin = plugin;
		teleportBackWhenLimitViolate = plugin.getDatabaseHandler().getConfig().getBoolean("teleport-back-when-arena-cuboid-violated", false);
	}

	private boolean isUser(final Player player) {
		final IUser user = DataHandler.getUser(player.getUniqueId());
		return user != null;
	}

	private boolean isUser(final Entity entity) {
		if (entity instanceof Player) {
			return isUser((Player) entity);
		}
		return false;
	}

	@EventHandler
	public void onMultiPlace(final BlockMultiPlaceEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		if (isUser(e.getPlayer())) {
			if (e.getAction() == Action.PHYSICAL || (e.getClickedBlock() != null && (e.getClickedBlock().getState() instanceof InventoryHolder || e.getClickedBlock().getType().equals(Material.WOOD_BUTTON) || e.getClickedBlock().getType().equals(Material.STONE_BUTTON)))) {
				e.setCancelled(true);
			} else if (e.getClickedBlock() != null) {
				switch (e.getClickedBlock().getType()) {
					case WORKBENCH:
					case ANVIL:
					case TRAP_DOOR:
					case FENCE_GATE:
					case DARK_OAK_DOOR:
					case ACACIA_DOOR:
					case BIRCH_DOOR:
					case JUNGLE_DOOR:
					case SPRUCE_DOOR:
					case WOOD_DOOR:
					case WOODEN_DOOR:
					case STONE_BUTTON:
					case WOOD_BUTTON:
					case LEVER:
						e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPickup(final PlayerPickupItemEvent e) {
		if (DataHandler.getSpectator(e.getPlayer().getUniqueId()) == null) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerItemDamage(final PlayerItemDamageEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null || e.getPlayer().isOp()) return;
		if (e.getMessage().startsWith("/")) {
			if (!plugin.getDatabaseHandler().getConfig().getStringList("executable-commands-by-player." + member.getTeam().getGame().getGameState().name()).contains(e.getMessage())) {
				e.setCancelled(true);
				MessageUtils.sendMessage(e.getPlayer(), "command-is-blocked", new PlaceholderUtil().add("{command}", e.getMessage()));
			}
		}
	}

	@EventHandler
	public void onFishing(final PlayerFishEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent e) {
		final IUser user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user == null) return;

		if (!user.getGame().getSpectatorManager().isSpectator(e.getPlayer())) {
			if (!user.getGame().getArena().getArenaCuboid().isIn(e.getBlock().getLocation())) {
				e.setCancelled(true);
				MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
				if (teleportBackWhenLimitViolate) {
					e.getPlayer().teleport(user.getGame().getArena().getPositions().get("spawn-team-1").getLocation(1));
				}
			}
		} else {
			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlace(final BlockPlaceEvent e) {
		final IUser user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user == null) return;
		if (!user.getGame().getSpectatorManager().isSpectator(e.getPlayer())) {
			if (!user.getGame().getArena().getArenaCuboid().isIn(e.getBlock().getLocation())) {
				e.setCancelled(true);
				MessageUtils.sendMessage(e.getPlayer(), "you-can-not-place");
				if (teleportBackWhenLimitViolate) {
					e.getPlayer().teleport(user.getGame().getArena().getPositions().get("spawn-team-1").getLocation(1));
				}
			}
		} else {
			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent e) {
		if (e.getEntity().getType().equals(EntityType.PLAYER)) {
			final IUser user = DataHandler.getUser(e.getEntity().getUniqueId());
			if (user == null) return;

			if (user.getGame().getSpectatorManager().isSpectator((Player) e.getEntity())) {
				e.setCancelled(true);
				return;
			}

			if (e instanceof EntityDamageByEntityEvent) {
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
					if (!isUser(((EntityDamageByEntityEvent) e).getDamager())) {
						//victim is in duel
						//attacker isn't in duel
						//cancel it.
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
