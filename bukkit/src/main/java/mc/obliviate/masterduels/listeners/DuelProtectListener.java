package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class DuelProtectListener implements Listener {

	private final boolean teleportBackWhenLimitViolate;
	private final PickupAction pickupAction;
	private final double soupRegenAmount;

	public DuelProtectListener() {
		this.teleportBackWhenLimitViolate = ConfigurationHandler.getConfig().getBoolean("teleport-back-when-arena-cuboid-violated", false);
		this.pickupAction = PickupAction.valueOf(ConfigurationHandler.getConfig().getString("action-limitations.item-pickup", "DISALLOW"));
		this.soupRegenAmount = ConfigurationHandler.getConfig().getDouble("soup-regeneration-amount", 3.5d);
	}

	private boolean isUser(final Player player) {
		final IUser user = UserHandler.getUser(player.getUniqueId());
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
		final IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Spectator) {
			e.setCancelled(true);
		} else if (user instanceof Member) {
			if (e.getAction() == Action.PHYSICAL || (e.getClickedBlock() != null && (e.getClickedBlock().getState() instanceof InventoryHolder))) {
				e.setCancelled(true);
				return;
			}
			if (e.getClickedBlock() != null) {
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
	public void onSoupConsume(final PlayerInteractEvent e) {
		if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
		final IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Member) {
			if (e.getItem() != null && e.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
				e.getPlayer().setHealth(Math.min(e.getPlayer().getMaxHealth(), e.getPlayer().getHealth() + soupRegenAmount));
				e.getPlayer().setItemInHand(new ItemStack(Material.BOWL));
			}
		}
	}

	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Member) {
			switch (this.pickupAction) {
				case DISALLOW:
					e.setCancelled(true);
				case FRIENDLY:
					e.getItemDrop().setMetadata("team", new FixedMetadataValue(MasterDuels.getInstance(), ((Member) user).getTeam().getTeamId()));
			}
		} else if (user instanceof Spectator) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(final PlayerPickupItemEvent e) {
		IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());
		if (user instanceof Member) {
			List<MetadataValue> metas = e.getItem().getMetadata("team");
			final int mode = metas.size() == 0 ? -1 : metas.get(0).asInt();
			if (mode < 0 || mode == ((Member) user).getTeam().getTeamId()) return;
			e.setCancelled(true);
		} else if (user instanceof Spectator) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerItemDamage(final PlayerItemDamageEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		final Member member = UserHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null || e.getPlayer().isOp()) return;
		if (!e.getMessage().startsWith("/")) return;
		if (!ConfigurationHandler.getConfig().getStringList("action-limitations.executable-commands-during-match." + member.getTeam().getMatch().getMatchState().getMatchStateType().name()).contains(e.getMessage())) {
			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "command-is-blocked", new PlaceholderUtil().add("{command}", e.getMessage()));
		}
	}

	@EventHandler
	public void onFishing(final PlayerFishEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent e) {
		final IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());

		if (user instanceof Member) {
			final Arena arena = ((Member) user).getMatch().getArena();
			if (arena.getArenaCuboid().isIn(e.getBlock().getLocation())) return;

			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
			if (teleportBackWhenLimitViolate) {
				e.getPlayer().teleport(arena.getPositions().get("spawn-team-1").getLocation(1));
			}

		} else if (user instanceof Spectator) {
			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlace(final BlockPlaceEvent e) {
		final IUser user = UserHandler.getUser(e.getPlayer().getUniqueId());

		if (user instanceof Member) {
			final Arena arena = ((Member) user).getMatch().getArena();
			if (arena.getArenaCuboid().isIn(e.getBlock().getLocation())) return;

			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-place");
			if (teleportBackWhenLimitViolate) {
				e.getPlayer().teleport(arena.getPositions().get("spawn-team-1").getLocation(1));
			}

		} else if (user instanceof Spectator) {
			e.setCancelled(true);
			MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
		}
	}

	private enum PickupAction {
		DISALLOW,
		FRIENDLY,
		ALLOW
	}

	//todo victim is in duel
	// attacker isn't in duel
	// cancel it.

}
