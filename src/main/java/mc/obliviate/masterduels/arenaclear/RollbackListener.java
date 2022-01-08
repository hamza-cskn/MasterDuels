package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.events.arena.DuelArenaUninstallEvent;
import mc.obliviate.masterduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class RollbackListener implements Listener {

	private final MasterDuels plugin;
	private final boolean preventNonPlacedBlocks;

	public RollbackListener(MasterDuels plugin, boolean preventNonPlacedBlocks) {
		this.plugin = plugin;
		this.preventNonPlacedBlocks = preventNonPlacedBlocks;
	}

	@EventHandler
	public void onGameStart(final DuelGameStartEvent event) {
		plugin.getArenaClearHandler().add(event.getGame(), plugin);
	}

	@EventHandler
	public void onGameEnd(final DuelArenaUninstallEvent event) {
		plugin.getArenaClearHandler().getArenaClear(event.getGame().getArena().getName()).clear();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockPlaced();
		plugin.getArenaClearHandler().getArenaClear(arena.getName()).addBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
		if (preventNonPlacedBlocks) {
			e.getBlock().setMetadata("placedByPlayer", new FixedMetadataValue(plugin, true));
		}
	}

	@EventHandler
	public void onBlockForm(BlockFromToEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final Block block = e.getBlock();
				switch (e.getToBlock().getType()) {
					case OBSIDIAN:
					case COBBLESTONE:
					case STONE:
						break;
					default:
						return;
				}
				final Arena arena = Arena.getArenaAt(block.getLocation());
				if (arena == null) return;
				final ArenaClear arenaClear = plugin.getArenaClearHandler().getArenaClear(arena.getName());
				if (arenaClear == null) return;
				plugin.getArenaClearHandler().getArenaClear(arena.getName()).addBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
				if (preventNonPlacedBlocks) {
					e.getBlock().setMetadata("placedByPlayer", new FixedMetadataValue(plugin, true));
				}

			}
		}.runTaskLater(plugin, 1);

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (preventNonPlacedBlocks) {
			final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
			if (member == null) return;
			if (e.getBlock().getMetadata("placedByPlayer").isEmpty()) {
				e.setCancelled(true);
				MessageUtils.sendMessage(e.getPlayer(), "you-can-not-break");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockClicked();
		final BlockFace face = e.getBlockFace();
		plugin.getArenaClearHandler().getArenaClear(arena.getName()).addLiquid(block.getX() + face.getModX(), block.getY() + face.getModY(), block.getZ() + face.getModZ(), block.getWorld().getUID());
	}


}
