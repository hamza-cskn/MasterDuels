package mc.obliviate.masterduels.playerdata.history.gui;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.pagination.PaginationManager;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.playerdata.history.MatchHistoryLog;
import mc.obliviate.masterduels.playerdata.history.PlayerHistoryLog;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.*;

import static mc.obliviate.masterduels.utils.Utils.getPlaceholders;

public class DuelHistoryLogGui extends ConfigurableGui {

    private static DuelHistoryLogGui.Config guiConfig;
    private final PaginationManager paginationManager = new PaginationManager(this);

    public DuelHistoryLogGui(Player player) {
        super(player, "duel-history-log-gui");

        this.paginationManager.getSlots().addAll(guiConfig.pageSlots);

        List<MatchHistoryLog> logs = SQLManager.loadDuelHistories(); //todo cache it
        int i = 1;
        for (MatchHistoryLog log : logs) {
            Icon icon;
            if (log == null) {
                icon = new Icon(XMaterial.BEDROCK.parseItem()).setName(MessageUtils.parseColor("&cCould not load"));
            } else {
                PlaceholderUtil placeholderUtil = new PlaceholderUtil()
                        .add("{played-date}", TimerUtils.formatDate(log.getStartTime()))
                        .add("{match-duration-timer}", TimerUtils.formatTimeDifferenceAsTimer(log.getStartTime(), log.getFinishTime()))
                        .add("{match-duration-time}", TimerUtils.formatTimeDifferenceAsTime(log.getStartTime(), log.getFinishTime()))
                        .add("{played-rounds}", log.getPlayedRound() + "");

                if (log.getPlayerHistoryLogMap().size() <= 2) {
                    icon = getSoloGamesIcon(log, placeholderUtil, i);
                } else {
                    icon = getNonSoloGamesIcon(log, placeholderUtil, i);
                }
            }

            paginationManager.addItem(icon);
            i++;
        }
    }

    private Icon getNonSoloGamesIcon(MatchHistoryLog log, PlaceholderUtil placeholderUtil, int amount) {
        Icon icon = new Icon(getConfigItem("non-solo-games-icon", placeholderUtil)).setAmount(amount);
        List<String> loreCopy = new ArrayList<>();
        for (String loreLine : icon.getItem().getItemMeta().getLore()) {
            if (loreLine.equalsIgnoreCase("{+winners}")) {
                loreCopy.add(formatPersons(log.getWinners(), "winner"));
                /*for (Map.Entry<UUID, PlayerHistoryLog> entry : log.getPlayerHistoryLogMap().entrySet()) {
                    PlayerHistoryLog playerHistoryLog = entry.getValue();
                    OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());

                    if (playerHistoryLog == null) continue;
                    PlaceholderUtil placeholders = getPlaceholders(playerHistoryLog);

                    placeholders.add("{player}", p.getName());

                    if (log.getWinners() != null && log.getWinners().contains(p.getUniqueId())) {
                        loreCopy.addAll(MessageUtils.parseColor(MessageUtils.applyPlaceholders(getIconsSection("non-solo-games-icon").getStringList("winner-info-format"), placeholders)));
                    } else {
                        loreCopy.addAll(MessageUtils.parseColor(MessageUtils.applyPlaceholders(getIconsSection("non-solo-games-icon").getStringList("loser-info-format"), placeholders)));
                    }
                }
*/
            } else if (loreLine.equalsIgnoreCase("{+losers}")) {
                loreCopy.add(formatPersons(log.getLosers(), "loser"));
            } else {
                loreCopy.add(loreLine);
            }
        }
        return icon.setLore(loreCopy);
    }

    private Icon getSoloGamesIcon(MatchHistoryLog log, PlaceholderUtil placeholderUtil, int amount) {
        Icon icon = new Icon(getConfigItem("solo-games-icon", placeholderUtil)).setAmount(amount);

        OfflinePlayer player1 = null;
        PlayerHistoryLog player1HistoryLog = null;
        OfflinePlayer player2 = null;
        PlayerHistoryLog player2HistoryLog = null;

        for (Map.Entry<UUID, PlayerHistoryLog> entry : log.getPlayerHistoryLogMap().entrySet()) {
            if (player1 == null) {
                player1 = Bukkit.getOfflinePlayer(entry.getKey());
                player1HistoryLog = entry.getValue();
            } else {
                player2 = Bukkit.getOfflinePlayer(entry.getKey());
                player2HistoryLog = entry.getValue();
            }
        }

        List<String> loreCopy = new ArrayList<>();
        for (String loreLine : icon.getItem().getItemMeta().getLore()) {
            if (loreLine.equalsIgnoreCase("{+player-1}") || loreLine.equalsIgnoreCase("{+player-2}")) {

                PlayerHistoryLog playerHistoryLog = null;
                OfflinePlayer p = null;

                if (loreLine.equalsIgnoreCase("{+player-1}")) {
                    playerHistoryLog = player1HistoryLog;
                    p = player1;
                } else if (loreLine.equalsIgnoreCase("{+player-2}")) {
                    playerHistoryLog = player2HistoryLog;
                    p = player2;
                }

                PlaceholderUtil placeholders = getPlaceholders(playerHistoryLog);
                if (playerHistoryLog == null) continue;

                placeholders.add("{player}", p.getName());

                if (log.getWinners() != null && log.getWinners().contains(p.getUniqueId())) {
                    loreCopy.addAll(MessageUtils.parseColor(MessageUtils.applyPlaceholders(getIconsSection("solo-games-icon").getStringList("winner-info-format"), placeholders)));
                } else {
                    loreCopy.addAll(MessageUtils.parseColor(MessageUtils.applyPlaceholders(getIconsSection("solo-games-icon").getStringList("loser-info-format"), placeholders)));
                }

            } else {
                loreCopy.add(loreLine);
            }
        }

        return icon.setLore(loreCopy);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putDysfunctionalIcons(Arrays.asList("previous", "next"));
        if (!this.paginationManager.isFirstPage()) {
            putIcon("previous", e -> this.paginationManager.goPreviousPage().update());
        }
        if (!this.paginationManager.isLastPage()) {
            putIcon("next", e -> this.paginationManager.goNextPage().update());
        }
        this.paginationManager.update();
    }

    @Override
    public String getSectionPath() {
        return "game-history-gui";
    }


    public static class Config {

        private final List<Integer> pageSlots;

        public Config(List<Integer> pageSlots) {
            this.pageSlots = pageSlots;
            DuelHistoryLogGui.guiConfig = this;
        }
    }

    public String formatPersons(List<UUID> list, String formatType) {
        String format = getIconsSection("non-solo-games-icon").getString(list.size() + "-person-format");
        if (format == null) getIconsSection("non-solo-games-icon").getString("more-person-format");
        Preconditions.checkNotNull(format, "no person format found");
        int left = list.size();
        for (int i = 0; i < list.size(); i++) {
            if (format.contains("{" + i + "}")) {
                left -= 1;
                String playerFormat = getIconsSection("non-solo-games-icon").getString(formatType + "-info-format");
                format = format.replace("{" + i + "}", playerFormat.replace("{player}", Bukkit.getOfflinePlayer(list.get(i)).getName()));
            }
        }
        return format.replace("{left}", String.valueOf(left));
    }
}
