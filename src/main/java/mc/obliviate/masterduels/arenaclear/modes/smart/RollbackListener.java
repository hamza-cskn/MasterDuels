package mc.obliviate.masterduels.arenaclear.modes.smart;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
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
	public void onBlockPlace(BlockPlaceEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockPlaced();
		final SmartArenaClear arenaClear = (SmartArenaClear) plugin.getArenaClearHandler().getArenaClear(arena.getName());
		arenaClear.addBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
		//todo move prevent non placed blocks methods to duel protect listener
		if (preventNonPlacedBlocks) {
			e.getBlock().setMetadata("placedByPlayer", new FixedMetadataValue(plugin, true));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	//todo optimized block from to simulation
	public void onBlockForm(BlockFromToEvent e) {
		final Block block = e.getToBlock();
		final Arena arena = Arena.getArenaAt(block.getLocation());
		if (arena == null) return;
		final SmartArenaClear arenaClear = (SmartArenaClear) plugin.getArenaClearHandler().getArenaClear(arena.getName());
		if (arenaClear == null) return;

		//check is it destroy event
		switch (e.getToBlock().getType()) {
			case AIR:
			case WATER:
			case STATIONARY_WATER:
			case LAVA:
			case STATIONARY_LAVA:
				break;
			default:
				e.setCancelled(true);
				return;
		}

		//wait and check is it a transform event
		new BukkitRunnable() {
			@Override
			public void run() {
				switch (block.getType()) {
					case OBSIDIAN:
					case COBBLESTONE:
					case STONE:
						break;
					default:
						return;
				}
				arenaClear.addBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
				if (preventNonPlacedBlocks) {
					block.setMetadata("placedByPlayer", new FixedMetadataValue(plugin, true));
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
		final SmartArenaClear arenaClear = (SmartArenaClear) plugin.getArenaClearHandler().getArenaClear(arena.getName());
		arenaClear.addLiquid(block.getX() + face.getModX(), block.getY() + face.getModY(), block.getZ() + face.getModZ(), block.getWorld().getUID());
	}


}
