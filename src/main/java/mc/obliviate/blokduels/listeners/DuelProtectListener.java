package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.Spectator;
import mc.obliviate.blokduels.user.User;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

public class DuelProtectListener implements Listener {

	private final BlokDuels plugin;

	public DuelProtectListener(BlokDuels plugin) {
		this.plugin = plugin;
	}

	private boolean isUser(final Player player) {
		final User user = DataHandler.getUser(player.getUniqueId());
		return user != null;
	}

	private boolean isUser(final Entity entity) {
		if (entity instanceof Player) {
			return isUser((Player) entity);
		}
		return false;
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent e) {
		final User user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user == null) return;
		if (user instanceof Spectator) {
			e.setCancelled(true);
			return;
		}
		if (!((Member) user).getTeam().getGame().getPlacedBlocks().contains(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlace(final BlockPlaceEvent e) {
		final User user = DataHandler.getUser(e.getPlayer().getUniqueId());
		if (user == null) return;
		if (user instanceof Spectator) {
			e.setCancelled(true);
			return;
		}
		((Member) user).getTeam().getGame().addPlacedBlock(e.getBlockPlaced().getLocation());
	}

	@EventHandler
	public void onMultiPlace(final BlockMultiPlaceEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		if (isUser(e.getPlayer())) {
			//fixme crafting, anvil, trapdoor and fence gates are openable
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

	//fixme liquids does not cleans after game end
	@EventHandler
	public void onBucketFill(final PlayerBucketFillEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(final PlayerBucketFillEvent e) {
		if (!isUser(e.getPlayer())) return;
		e.setCancelled(true);
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

	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (e.getEntity().getType().equals(EntityType.PLAYER) && e.getDamager().getType().equals(EntityType.PLAYER)) {
			if (isUser(e.getEntity())) {
				if (!isUser(e.getDamager())) {
					//victim is in duel
					//attacker isn't in duel
					//cancel it.
					e.setCancelled(true);
				}
			}
		}
	}
}
