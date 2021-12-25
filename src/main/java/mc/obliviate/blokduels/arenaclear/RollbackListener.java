package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.api.events.arena.DuelGameFinishEvent;
import mc.obliviate.blokduels.api.events.arena.DuelGameStartEvent;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.arenaclear.workloads.BlockWorkLoad;
import mc.obliviate.blokduels.arenaclear.workloads.LiquidWorkload;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.team.Member;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.HashMap;
import java.util.Map;

public class RollbackListener implements Listener {

	private final BlokDuels api;

	private final Map<String, WorkLoadThread> workLoadThreadMap = new HashMap<>();

	public RollbackListener(BlokDuels api) {
		this.api = api;
	}

	@EventHandler
	public void onMatchStart(final DuelGameStartEvent event) {
		workLoadThreadMap.put(event.getGame().getArena().getName(), new WorkLoadThread(api));
	}

	@EventHandler
	public void onMatchEnd(final DuelGameFinishEvent event) {
		final WorkLoadThread thread = workLoadThreadMap.get(event.getGame().getArena().getName());
		thread.run();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockPlaced();
		workLoadThreadMap.get(arena.getName()).addWorkLoad(new BlockWorkLoad(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		final Arena arena = member.getGame().getArena();
		final Block block = e.getBlockClicked();
		final BlockFace face = e.getBlockFace();
		workLoadThreadMap.get(arena.getName()).addWorkLoad(new LiquidWorkload(block.getX() + face.getModX(), block.getY() + face.getModY(), block.getZ() + face.getModZ(), e.getBlockClicked().getWorld().getUID()));

	}

}
