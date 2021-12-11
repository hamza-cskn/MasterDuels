package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.team.Member;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

	private boolean isMember(final Player player) {
		final Member member = DataHandler.getMember(player.getUniqueId());
		return member != null;
	}

	private boolean isMember(final Entity entity) {
		if (entity instanceof Player) {
			return isMember((Player) entity);
		}
		return false;
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getTeam().getGame().getPlacedBlocks().contains(e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlace(final BlockPlaceEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		member.getTeam().getGame().addPlacedBlock(e.getBlockPlaced().getLocation());
	}

	@EventHandler
	public void onMultiPlace(final BlockMultiPlaceEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		if (isMember(e.getPlayer())) {
			//fixme crafting, anvil, trapdoor and fence gates are openable
			if (e.getAction() == Action.PHYSICAL || (e.getClickedBlock() != null && (e.getClickedBlock().getState() instanceof InventoryHolder || e.getClickedBlock().getType().equals(Material.WOOD_BUTTON) || e.getClickedBlock().getType().equals(Material.STONE_BUTTON)))) {
				e.setCancelled(true);
			} else {
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
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(final PlayerBucketFillEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerItemDamage(final PlayerItemDamageEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null || e.getPlayer().isOp()) return;
		if (e.getMessage().startsWith("/")) {
			if (!plugin.getDatabaseHandler().getConfig().getStringList("executable-commands." + member.getTeam().getGame().getGameState().name()).contains(e.getMessage())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("bu komutu burada kullanamazsın.");
			}
		}

	}

	@EventHandler
	public void onFishing(final PlayerFishEvent e) {
		if (!isMember(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (isMember(e.getEntity())) {
			if (!isMember(e.getDamager())) {
				//victim is in duel
				//attacker isn't in duel
				//cancel it.
				e.setCancelled(true);
			}
		}
	}
}
