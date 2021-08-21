package mc.obliviate.blokduels.listeners;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.data.DatabaseHandler;
import mc.obliviate.blokduels.team.Member;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public class DuelProtectListener implements Listener {

	private boolean isAccessed(final Player player) {
		final Member member = DataHandler.getMember(player.getUniqueId());
		return member != null;
	}

	private boolean isAccessed(final Entity entity) {
		if (entity instanceof Player) {
			return isAccessed((Player) entity);
		}
		return false;
	}

	@EventHandler
	public void onBreak(final BlockBreakEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlace(final BlockPlaceEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onMultiPlace(final BlockMultiPlaceEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		if (isAccessed(e.getPlayer())) {
			switch (e.getAction()) {
				case LEFT_CLICK_BLOCK:
				case RIGHT_CLICK_BLOCK:
				case PHYSICAL:
					e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBucketFill(final PlayerBucketFillEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(final PlayerBucketFillEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(final PlayerDropItemEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerItemDamage(final PlayerItemDamageEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		if (e.getMessage().startsWith("/")) {
			if (!DatabaseHandler.getConfig().getStringList("executable-commands").contains(e.getMessage())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage("bu komutu burada kullanamazsın.");
			}
		}

	}

	@EventHandler
	public void onFishing(final PlayerFishEvent e) {
		if (!isAccessed(e.getPlayer())) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (isAccessed(e.getEntity())) {
			if (!isAccessed(e.getDamager())) {
				//victim is in duel
				//attacker isn't in duel
				//cancel it.
				e.setCancelled(true);
			}
		}
	}
}
