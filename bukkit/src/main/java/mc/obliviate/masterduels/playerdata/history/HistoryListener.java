package mc.obliviate.masterduels.playerdata.history;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchUninstallEvent;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.round.MatchRoundData;
import mc.obliviate.masterduels.playerdata.ProjectileLogEntry;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        for (Player player : event.getMatch().getPlayers()) {
            PlayerHistoryLog playerHistoryLog = log.getPlayerHistoryLogMap().get(player.getUniqueId());
            DuelStatistic duelStatistic = UserHandler.getUser(player.getUniqueId()).getStatistic();
            duelStatistic.migrate(playerHistoryLog.getPlayerData());
            addWinOrLose(event.getMatch().getGameDataStorage().getRoundData(), duelStatistic);
            plugin.getSqlManager().saveStatistic(duelStatistic);
        }
    }

    private void addWinOrLose(MatchRoundData roundData, DuelStatistic duelStatistic) {
        if (!roundData.didAnyTeamWin()) return;
        for (final Member member : roundData.getWinnerTeam().getMembers()) {
            if (member.getPlayer().getUniqueId().equals(duelStatistic.getPlayerUniqueId())) {
                duelStatistic.setWins(duelStatistic.getWins() + 1);
                return;
            }
        }
        duelStatistic.setLosses(duelStatistic.getLosses() + 1);
    }

    @EventHandler
    public void onMatchUninstall(DuelMatchUninstallEvent event) {
        final MatchHistoryLog log = MatchHistoryLog.getSavingMatchHistoryLogs().get(event.getMatch());
        log.uninstall(event.isNaturalUninstall());
        SQLManager.saveDuelHistory(log);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                final Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
                final PlayerHistoryLog playerLog = MatchHistoryLog.getPlayerHistory(attacker);
                if (playerLog == null) return;

                playerLog.getPlayerData().setDamageDealt(playerLog.getPlayerData().getDamageDealt() + ((int) event.getFinalDamage() * 10));

            } else if (event.getDamager() instanceof Player) {
                final PlayerHistoryLog playerLog = MatchHistoryLog.getPlayerHistory((Player) event.getDamager());
                if (playerLog == null) return;

                playerLog.getPlayerData().setDamageDealt(playerLog.getPlayerData().getDamageDealt() + ((int) event.getFinalDamage() * 10));
                playerLog.getPlayerData().setHitClick(playerLog.getPlayerData().getHitClick() + 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        final PlayerHistoryLog playerLog = MatchHistoryLog.getPlayerHistory(event.getPlayer());
        if (playerLog == null) return;

        playerLog.getPlayerData().setClick(playerLog.getPlayerData().getClick() + 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        final PlayerHistoryLog log = MatchHistoryLog.getPlayerHistory(event.getPlayer());
        if (log == null) return;

        log.getPlayerData().setBrokenBlocks(log.getPlayerData().getBrokenBlocks() + 1);
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        final PlayerHistoryLog log = MatchHistoryLog.getPlayerHistory(event.getPlayer());
        if (log == null) return;
        log.getPlayerData().setPlacedBlocks(log.getPlayerData().getPlacedBlocks() + 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final PlayerHistoryLog log = MatchHistoryLog.getPlayerHistory(((Player) event.getEntity().getShooter()));
        if (log == null) return;
        ProjectileLogEntry projectileLogEntry = log.getPlayerData().getProjectileLog(event.getEntity());
        if (projectileLogEntry == null) return;
        projectileLogEntry.increaseThrew(1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final PlayerHistoryLog log = MatchHistoryLog.getPlayerHistory(((Player) event.getEntity().getShooter()));
        if (log == null) return;
        ProjectileLogEntry projectileLogEntry = log.getPlayerData().getProjectileLog(event.getEntity());
        if (projectileLogEntry == null) return;
        projectileLogEntry.increaseHit(1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHealthRegeneration(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final PlayerHistoryLog log = MatchHistoryLog.getPlayerHistory(((Player) event.getEntity()));
        if (log == null) return;
        log.getPlayerData().setRegeneratedHealth(log.getPlayerData().getRegeneratedHealth() + event.getAmount());
    }
}
