package mc.obliviate.masterduels.playerdata.statistics.gui;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class StatisticGui extends ConfigurableGui {

    private final DuelStatistic duelStatistic;

    public StatisticGui(Player player, DuelStatistic duelStatistic) {
        super(player, "statistic-gui");
        Preconditions.checkNotNull(duelStatistic, "statistics cannot be null");
        this.duelStatistic = duelStatistic;
        Bukkit.broadcastMessage("opened: " + duelStatistic);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        PlaceholderUtil placeholderUtil = new PlaceholderUtil()
                .add("{wins}", this.duelStatistic.getWins() + "")
                .add("{losses}", this.duelStatistic.getLosses() + "")
                .add("{placed-blocks}", this.duelStatistic.getPlayerData().getPlacedBlocks() + "");
        putDysfunctionalIcons(placeholderUtil);
    }
}
