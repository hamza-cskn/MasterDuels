package mc.obliviate.masterduels.history;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchUninstallEvent;
import mc.obliviate.masterduels.data.SQLManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mc.obliviate.masterduels.history.MatchHistoryLog.getPlayerHistory;

@SuppressWarnings("rawtypes")
public class HistoryListener implements Listener {

	private final MasterDuels plugin;

	public HistoryListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onMatchStart(DuelMatchStartEvent event) {
		new MatchHistoryLog(event.getMatch()).start();
	}

	@EventHandler
	public void onMatchEnd(DuelMatchEndEvent event) {
		final MatchHistoryLog log = MatchHistoryLog.getSavingMatchHistoryLogs().get(event.getMatch());

		if (!event.isNaturalEnding()) return;
		List<UUID> winnerUUIDs = new ArrayList<>();
		event.getMatch().getGameDataStorage().getGameRoundData().getWinnerTeam().getMembers().forEach(member -> {
			winnerUUIDs.add(member.getPlayer().getUniqueId());
		});

		log.finish(winnerUUIDs);
	}

	@EventHandler
	public void onMatchUninstall(DuelMatchUninstallEvent event) {
		final MatchHistoryLog log = MatchHistoryLog.getSavingMatchHistoryLogs().get(event.getMatch());
		log.uninstall(event.isNaturalUninstall());
		SQLManager.saveDuelHistory(log);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final PlayerHistoryLog log = getPlayerHistory((Player) event.getDamager());
			if (log == null) return;

			log.setHitClick(log.getHitClick() + 1);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_AIR) return;
		final PlayerHistoryLog log = getPlayerHistory(event.getPlayer());
		if (log == null) return;

		log.setClick(log.getClick() + 1);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final PlayerHistoryLog log = getPlayerHistory(event.getPlayer());
		if (log == null) return;

		log.setBrokenBlocks(log.getBrokenBlocks() + 1);
	}


	@EventHandler
	public void onPlace(BlockBreakEvent event) {
		final PlayerHistoryLog log = getPlayerHistory(event.getPlayer());
		if (log == null) return;
		log.setPlacedBlocks(log.getPlacedBlocks() + 1);
	}


	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		final PlayerHistoryLog log = getPlayerHistory(((Player) event.getEntity().getShooter()));
		if (log == null) return;
		ProjectileLogEntry projectileLogEntry = log.getProjectileLog(event.getEntity());
		if (projectileLogEntry == null) return;
		projectileLogEntry.increaseThrew(1);
	}


	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) return;
		final PlayerHistoryLog log = getPlayerHistory(((Player) event.getEntity().getShooter()));
		if (log == null) return;
		ProjectileLogEntry projectileLogEntry = log.getProjectileLog(event.getEntity());
		if (projectileLogEntry == null) return;
		projectileLogEntry.increaseHit(1);
	}

	@EventHandler
	public void onHealthRegeneration(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		final PlayerHistoryLog log = getPlayerHistory(((Player) event.getEntity()));
		if (log == null) return;
		log.setRegeneratedHealth(log.getRegeneratedHealth() + event.getAmount());
	}
}
