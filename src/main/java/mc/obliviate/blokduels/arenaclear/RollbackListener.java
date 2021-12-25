package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.api.events.arena.DuelArenaUninstallEvent;
import mc.obliviate.blokduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.team.Member;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class RollbackListener implements Listener {

	private final BlokDuels plugin;
	private final boolean preventNonPlacedBlocks;

	public RollbackListener(BlokDuels plugin, boolean preventNonPlacedBlocks) {
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
	public void onBlockBreak(BlockBreakEvent e) {
		if (preventNonPlacedBlocks) {
			final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
			if (member == null) return;
			if (e.getBlock().getMetadata("placedByPlayer").isEmpty()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("You can not break this block.");
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
