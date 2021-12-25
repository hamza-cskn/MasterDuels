package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.api.events.arena.DuelGameFinishEvent;
import mc.obliviate.blokduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.team.Member;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class RollbackListener implements Listener {

	private final BlokDuels api;

	public RollbackListener(BlokDuels api) {
		this.api = api;
	}

	@EventHandler
	public void onGameStart(final DuelGameStartEvent event) {
		api.getArenaClearHandler().add(event.getGame(), api);
	}

	@EventHandler
	public void onGameEnd(final DuelGameFinishEvent event) {
		api.getArenaClearHandler().getArenaClear(event.getGame().getArena().getName()).clear();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockPlaced();
		api.getArenaClearHandler().getArenaClear(arena.getName()).addBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockClicked();
		final BlockFace face = e.getBlockFace();
		api.getArenaClearHandler().getArenaClear(arena.getName()).addLiquid(block.getX() + face.getModX(), block.getY() + face.getModY(), block.getZ() + face.getModZ(), block.getWorld().getUID());
	}

}
